package com.alfaazplus.sunnah.ui.utils.app

import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.api.RetrofitInstance
import com.alfaazplus.sunnah.api.models.ResourcesVersions
import com.alfaazplus.sunnah.ui.utils.createFile
import com.alfaazplus.sunnah.ui.utils.getOtherDirectory
import com.alfaazplus.sunnah.ui.utils.readFileText
import com.alfaazplus.sunnah.ui.utils.writeFileText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.CancellationException as JavaCancellationException

enum class ResourceUpdateState {
    IDLE,
    CHECKING,
    UPDATING,
    COMPLETED,
    FAILED
}

object ResourceUpdateManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _updateState = MutableStateFlow(ResourceUpdateState.IDLE)
    val updateState: StateFlow<ResourceUpdateState> = _updateState.asStateFlow()

    fun ensureUpdatesChecked(force: Boolean = false) {
        scope.launch {
            checkAndPerformUpdates(force)
        }
    }

    fun getLocalVersions(): ResourcesVersions? {
        val file = getResourcesVersionsFile()
        if (!file.exists() || file.length() == 0L) return null

        return try {
            JsonHelper.json.decodeFromString<ResourcesVersions>(file.readFileText())
        } catch (e: Exception) {
            Logger.saveError(e, "ResourceUpdateManager.getLocalVersions")
            null
        }
    }

    private suspend fun checkAndPerformUpdates(force: Boolean = false) = withContext(Dispatchers.IO) {
        if (_updateState.value == ResourceUpdateState.CHECKING || _updateState.value == ResourceUpdateState.UPDATING) return@withContext

        _updateState.value = ResourceUpdateState.CHECKING

        try {
            val remoteVersions = RetrofitInstance.github.getResourcesVersions()
            val localVersions = getLocalVersions()

            if (force || localVersions == null || isAnyUpdateAvailable(
                    localVersions, remoteVersions
                )
            ) {
                _updateState.value = ResourceUpdateState.UPDATING

                Logger.d("Resources update available: ", remoteVersions)
                performUpdates(localVersions, remoteVersions, force)

                saveLocalVersions(remoteVersions)

                _updateState.value = ResourceUpdateState.COMPLETED
            } else {
                _updateState.value = ResourceUpdateState.IDLE
            }
        } catch (e: Exception) {
            if (e is CancellationException || e is JavaCancellationException) throw e
            Logger.saveError(e, "ResourceUpdateManager.checkAndPerformUpdates")
            _updateState.value = ResourceUpdateState.FAILED
        }
    }

    private fun isAnyUpdateAvailable(local: ResourcesVersions, remote: ResourcesVersions): Boolean {
        return remote.translations.any { (id, remoteVersion) ->
            remoteVersion > (local.translations[id] ?: 0)
        }
    }

    private suspend fun performUpdates(
        local: ResourcesVersions?,
        remote: ResourcesVersions,
        force: Boolean,
    ) = withContext(Dispatchers.IO) {
        /*supervisorScope {
            launch {

            }
        }*/
        // nothing to update for now, it will just prompt to the user
    }

    private fun saveLocalVersions(versions: ResourcesVersions) {
        try {
            val file = getResourcesVersionsFile()

            if (file.createFile()) {
                file.writeFileText(JsonHelper.json.encodeToString(versions))
            }
        } catch (e: Exception) {
            Logger.saveError(e, "ResourceUpdateManager.saveLocalVersions")
        }
    }

    private fun getResourcesVersionsFile(): File {
        return File(getOtherDirectory(), "resources_versions.json")
    }
}

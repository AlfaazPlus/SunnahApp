package com.alfaazplus.sunnah.ui.utils.message

import android.content.Context
import android.os.Build
import android.widget.Toast
import java.lang.ref.WeakReference

object MessageUtils {
    private var mToast: WeakReference<Toast>? = null

    fun showToast(context: Context, msgRes: Int, duration: Int) {
        showToast(context, context.getString(msgRes), duration)
    }

    fun showToast(context: Context, msg: CharSequence?, duration: Int) {
        try {
            mToast
                ?.get()
                ?.cancel()
        } catch (ignored: Exception) {
        }
        mToast = WeakReference(Toast.makeText(context, msg, duration))
        mToast
            ?.get()
            ?.show()
    }

    fun showNoInternet(ctx: Context, cancelable: Boolean = true, runOnDismiss: (() -> Unit)? = null) {/*val builder = PeaceDialog2.newBuilder(ctx)
        builder.setTitle(R.string.strTitleNoInternet)
        builder.setMessage(R.string.strMsgNoInternetLong)
        builder.setNeutralButton(R.string.strLabelClose, null)
        if (runOnDismiss != null) {
            builder.setOnDismissListener { runOnDismiss.run() }
        }
        builder.setCancelable(cancelable)
        builder.setFocusOnNeutral(true)
        builder.show()*/
    }

    fun showSomethingWrong(ctx: Context, runOnDismiss: (() -> Unit)? = null) {/*val builder = PeaceDialog2.newBuilder(ctx)
        builder.setTitle(R.string.strTitleError)
        builder.setMessage(R.string.strMsgSomethingWrong)
        builder.setNeutralButton(R.string.strLabelClose, null)
        if (runOnDismiss != null) {
            builder.setOnDismissListener { runOnDismiss.run() }
        }
        builder.setCancelable(true)
        builder.setFocusOnNeutral(true)
        builder.show()*/
    }

    fun show(context: Context, title: String, msg: String, btn: String, action: (() -> Unit)? = null) {/*val builder = PeaceDialog2.newBuilder(context)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setNeutralButton(btn) { _, _ -> action?.run() }
        builder.setFocusOnNeutral(true)
        builder.show()*/
    }

    fun showClipboardMessage(context: Context, text: String) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            showToast(context = context, msg = text, duration = Toast.LENGTH_SHORT)
        }
    }
}
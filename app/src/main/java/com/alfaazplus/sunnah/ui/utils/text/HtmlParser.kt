package com.alfaazplus.sunnah.ui.utils.text

import android.text.Editable
import android.text.Html
import org.xml.sax.Attributes
import org.xml.sax.ContentHandler
import org.xml.sax.Locator
import org.xml.sax.XMLReader

class HtmlParser(
    private val handler: HtmlTagHandler
) : Html.TagHandler, ContentHandler {
    private val tagStatus = ArrayDeque<Boolean>()
    private var wrapped: ContentHandler? = null
    private var text: Editable? = null
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        if (wrapped == null) {
            // record result object
            text = output
            // record current content handler
            wrapped = xmlReader.contentHandler
            // replace content handler with our own that forwards to calls to original when needed
            xmlReader.contentHandler = this
            // add false to the stack to make sure we always have a tag to pop
            tagStatus.addLast(false)
        }
    }

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        val isHandled = handler.handleTag(true, localName, text, attributes)
        tagStatus.addLast(isHandled)

        if (!isHandled) {
            wrapped?.startElement(uri, localName, qName, attributes)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if (!tagStatus.removeLast()) {
            wrapped?.endElement(uri, localName, qName)
        }
        handler.handleTag(false, localName, text, null)
    }

    override fun setDocumentLocator(locator: Locator) {
        wrapped?.setDocumentLocator(locator)
    }

    override fun startDocument() {
        wrapped?.startDocument()
    }

    override fun endDocument() {
        wrapped?.endDocument()
    }

    override fun startPrefixMapping(prefix: String, uri: String) {
        wrapped?.startPrefixMapping(prefix, uri)
    }

    override fun endPrefixMapping(prefix: String) {
        wrapped?.endPrefixMapping(prefix)
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        wrapped?.characters(ch, start, length)
    }

    override fun ignorableWhitespace(ch: CharArray, start: Int, length: Int) {
        wrapped?.ignorableWhitespace(ch, start, length)
    }

    override fun processingInstruction(target: String, data: String) {
        wrapped?.processingInstruction(target, data)
    }

    override fun skippedEntity(name: String) {
        wrapped?.skippedEntity(name)
    }
}

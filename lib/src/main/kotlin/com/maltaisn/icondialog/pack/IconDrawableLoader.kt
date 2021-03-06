/*
 * Copyright 2019 Nicolas Maltais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maltaisn.icondialog.pack

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.Xml
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.maltaisn.icondialog.data.Icon
import org.xmlpull.v1.XmlPullParser
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Class use to load icon drawables.
 * @param context Any context, needed to get resources.
 */
class IconDrawableLoader(context: Context) {

    private val context = context.applicationContext

    /**
     * Create the vector drawable for an [icon] and set it.
     */
    @SuppressLint("DiscouragedPrivateApi,PrivateApi")
    fun loadDrawable(icon: Icon) {
        if (icon.drawable != null) {
            // Icon drawable is already loaded.
            return
        }

        val drawable: Drawable?
        val binXml = createDrawableBinaryXml(icon.pathData, icon.width, icon.height)
        try {
            // Get the binary XML parser (XmlBlock.Parser) and use it to create the drawable
            // This should be equivalent to AssetManager#getXml()
            val xmlBlock = Class.forName("android.content.res.XmlBlock")
            val xmlBlockConstr = xmlBlock.getConstructor(ByteArray::class.java)
            val xmlParserNew = xmlBlock.getDeclaredMethod("newParser")
            xmlBlockConstr.isAccessible = true
            xmlParserNew.isAccessible = true
            val parser = xmlParserNew.invoke(xmlBlockConstr.newInstance(binXml as Any)) as XmlPullParser

            if (Build.VERSION.SDK_INT >= 24) {
                drawable = Drawable.createFromXml(context.resources, parser)
            } else {
                // Before API 24, vector drawables aren't rendered correctly without compat lib
                val attrs = Xml.asAttributeSet(parser)
                var type = parser.next()
                while (type != XmlPullParser.START_TAG) {
                    type = parser.next()
                }
                drawable = VectorDrawableCompat.createFromXmlInner(context.resources, parser, attrs, null)
            }

        } catch (e: Exception) {
            // Could not load icon.
            Log.e(TAG, "Could not create vector drawable for icon ID ${icon.id}.", e)
            return
        }

        icon.drawable = drawable
    }

    companion object {
        private val TAG = IconDrawableLoader::class.java.simpleName

        private val BIN_XML_START = shortArrayOf(
                0x03, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x1C, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x01, 0x00, 0x00, 0x44, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00, 0x11, 0x00, 0x00, 0x00,
                0x21, 0x00, 0x00, 0x00, 0x32, 0x00, 0x00, 0x00, 0x3E, 0x00, 0x00, 0x00,
                0x49, 0x00, 0x00, 0x00, 0x76, 0x00, 0x00, 0x00, 0x7D, 0x00, 0x00, 0x00,
                0x86, 0x00, 0x00, 0x00, 0x06, 0x06, 0x68, 0x65, 0x69, 0x67, 0x68, 0x74,
                0x00, 0x05, 0x05, 0x77, 0x69, 0x64, 0x74, 0x68, 0x00, 0x0D, 0x0D, 0x76,
                0x69, 0x65, 0x77, 0x70, 0x6F, 0x72, 0x74, 0x57, 0x69, 0x64, 0x74, 0x68,
                0x00, 0x0E, 0x0E, 0x76, 0x69, 0x65, 0x77, 0x70, 0x6F, 0x72, 0x74, 0x48,
                0x65, 0x69, 0x67, 0x68, 0x74, 0x00, 0x09, 0x09, 0x66, 0x69, 0x6C, 0x6C,
                0x43, 0x6F, 0x6C, 0x6F, 0x72, 0x00, 0x08, 0x08, 0x70, 0x61, 0x74, 0x68,
                0x44, 0x61, 0x74, 0x61, 0x00, 0x2A, 0x2A, 0x68, 0x74, 0x74, 0x70, 0x3A,
                0x2F, 0x2F, 0x73, 0x63, 0x68, 0x65, 0x6D, 0x61, 0x73, 0x2E, 0x61, 0x6E,
                0x64, 0x72, 0x6F, 0x69, 0x64, 0x2E, 0x63, 0x6F, 0x6D, 0x2F, 0x61, 0x70,
                0x6B, 0x2F, 0x72, 0x65, 0x73, 0x2F, 0x61, 0x6E, 0x64, 0x72, 0x6F, 0x69,
                0x64, 0x00, 0x04, 0x04, 0x70, 0x61, 0x74, 0x68, 0x00, 0x06, 0x06, 0x76,
                0x65, 0x63, 0x74, 0x6F, 0x72, 0x00)

        private val BIN_XML_END = shortArrayOf(
                0x80, 0x01, 0x08, 0x00, 0x20, 0x00, 0x00, 0x00, 0x55, 0x01, 0x01, 0x01,
                0x59, 0x01, 0x01, 0x01, 0x02, 0x04, 0x01, 0x01, 0x03, 0x04, 0x01, 0x01,
                0x04, 0x04, 0x01, 0x01, 0x05, 0x04, 0x01, 0x01, 0x02, 0x01, 0x10, 0x00,
                0x74, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x00, 0x14, 0x00, 0x14, 0x00,
                0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x05,
                0x01, 0x18, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00,
                0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x05, 0x01, 0x18, 0x00, 0x00,
                0x06, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF,
                0x08, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00,
                0x03, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x00, 0x02, 0x01, 0x10, 0x00, 0x4C, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
                0x07, 0x00, 0x00, 0x00, 0x14, 0x00, 0x14, 0x00, 0x02, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00,
                0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x1D, 0x00, 0x00, 0x00, 0xFF,
                0x06, 0x00, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x09, 0x00, 0x00, 0x00,
                0x08, 0x00, 0x00, 0x03, 0x09, 0x00, 0x00, 0x00, 0x03, 0x01, 0x10, 0x00,
                0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF, 0x07, 0x00, 0x00, 0x00, 0x03, 0x01, 0x10, 0x00,
                0x18, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFF, 0xFF, 0xFF, 0xFF,
                0xFF, 0xFF, 0xFF, 0xFF, 0x08, 0x00, 0x00, 0x00)

        /**
         * Create a vector drawable binary XML from [pathData] so that it can be parsed and created.
         * Path data should fit in a viewport of a [width] and a [height].
         *
         * See [https://justanapplication.wordpress.com/category/android/android-binary-xml/] and
         * [https://stackoverflow.com/a/49920860/5288316] for more documentation.
         */
        private fun createDrawableBinaryXml(pathData: String, width: Int, height: Int): ByteArray {
            val pathBytes = pathData.toByteArray()
            val pathSpLength = pathBytes.size + if (pathBytes.size > 127) 5 else 3
            var spPaddingLength = (BIN_XML_START.size + pathSpLength) % 4
            if (spPaddingLength != 0) spPaddingLength = 4 - spPaddingLength
            val totalLength = BIN_XML_START.size + pathSpLength + spPaddingLength + BIN_XML_END.size

            val bb = ByteBuffer.allocate(totalLength)
            bb.order(ByteOrder.LITTLE_ENDIAN)

            // Write XML chunk header and string pool
            for (b in BIN_XML_START) {
                bb.put(b.toByte())
            }

            // Write XML size and string pool size
            bb.position(4)
            bb.putInt(totalLength)
            bb.position(12)
            bb.putInt(BIN_XML_START.size - 8 + pathSpLength + spPaddingLength)

            bb.position(BIN_XML_START.size)

            // Write path data
            if (pathBytes.size > 127) {
                val high = (pathBytes.size and 0xFF00 or 0x8000 ushr 8).toByte()
                val low = (pathBytes.size and 0xFF).toByte()
                bb.put(high)
                bb.put(low)
                bb.put(high)
                bb.put(low)
            } else {
                val len = pathBytes.size.toByte()
                bb.put(len)
                bb.put(len)
            }
            bb.put(pathBytes)
            bb.put(0.toByte())

            // Padding to align on 32-bit
            if (spPaddingLength > 0) {
                bb.put(ByteArray(spPaddingLength))
            }

            // Write XML tag and attributes data
            val index = bb.position()
            for (b in BIN_XML_END) {
                bb.put(b.toByte())
            }

            // Write viewport size attributes
            bb.putInt(index + 124, width.toFloat().toRawBits())  // android:viewportWidth="..."
            bb.putInt(index + 144, height.toFloat().toRawBits())  // android:viewportHeight="..."

            return bb.array()
        }
    }

}

package com.github.panpf.sketch.test.datasource

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.github.panpf.sketch.datasource.ByteArrayDataSource
import com.github.panpf.sketch.datasource.DataFrom
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.sketch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ByteArrayDataSourceTest {

    @Test
    fun testConstructor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        val request = LoadRequest(context, "http://sample.jpeg")
        ByteArrayDataSource(
            sketch = sketch,
            request = request,
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertTrue(sketch === this.sketch)
            Assert.assertTrue(request === this.request)
            Assert.assertEquals(37, this.length())
            Assert.assertEquals(DataFrom.MEMORY, this.dataFrom)
        }
    }

    @Test
    fun testNewFileDescriptor() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertNull(newFileDescriptor())
        }
    }

    @Test
    fun testNewInputStream() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            newInputStream().close()
        }
    }

    @Test
    fun testToString() {
        val context = InstrumentationRegistry.getContext()
        val sketch = context.sketch
        ByteArrayDataSource(
            sketch = sketch,
            request = LoadRequest(context, "http://sample.jpeg"),
            dataFrom = DataFrom.MEMORY,
            data = "fd5717876ab046b8aa889c9aaac4b56c8j5f3".toByteArray()
        ).apply {
            Assert.assertEquals(
                "ByteArrayDataSource(from=MEMORY,length=37)",
                toString()
            )
        }
    }
}
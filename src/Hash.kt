import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class Hash {
    lateinit var value: ByteArray
        private set

    companion object {
        fun newInstance(buffer: ByteArray): Hash {
            val hash = Hash()
            hash.computeHash(buffer)
            return hash
        }

        fun create(buffer: String): Hash = newInstance(buffer.toByteArray(StandardCharsets.UTF_8))

        fun create(left: Hash, right: Hash): Hash = newInstance(join(left.value, right.value))

        fun join(a: ByteArray, b: ByteArray): ByteArray {
            val c = ByteArray(a.size + b.size)
            System.arraycopy(a, 0, c, 0, a.size)
            System.arraycopy(b, 0, c, a.size, b.size)
            return c
        }
    }

    private fun computeHash(buffer: ByteArray) {
        try {
            this.value = MessageDigest.getInstance("SHA-256").digest(buffer)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun equals(hash: Hash?): Boolean {
        var result = false
        if (hash != null) {
            result = Arrays.equals(this.value, hash.value)
        }
        return result
    }

    fun equals(hash: ByteArray): Boolean = Arrays.equals(this.value, hash)

    override fun hashCode(): Int = Arrays.hashCode(value)

    override fun toString(): String = Base64.getEncoder().encodeToString(this.value)
}


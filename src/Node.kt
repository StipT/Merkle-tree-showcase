import java.security.InvalidParameterException
import java.util.*

class Node {
    lateinit var hash: Hash
        private set
    private var leftNode: Node? = null
    private var rightNode: Node? = null
    private var parent: Node? = null

    constructor(hash: Hash) {
        this.hash = hash
    }

    constructor(left: Node, right: Node?) {
        this.leftNode = left
        this.rightNode = right
        this.leftNode!!.parent = this
        if (this.rightNode != null) this.rightNode!!.parent = this

        this.computeHash()
    }

    private fun computeHash() {
        if (this.rightNode == null) {
            this.hash = this.leftNode!!.hash
        } else {
            this.hash = Hash.newInstance(
                Hash.join(
                    this.leftNode!!.hash.value, this.rightNode!!.hash.value
                )
            )
        }
        if (this.parent != null) {
            this.parent!!.computeHash()
        }
    }

    override fun hashCode(): Int = Objects.hash(hash, leftNode, rightNode, parent)

    override fun toString(): String = hash.toString()



    fun equals(other: Node): Boolean = this.hash.equals(other.hash)

    fun getLeftNode(): Node? = leftNode

    fun getRightNode(): Node? = rightNode

    fun getParent(): Node? = parent

    fun setLeftNode(node: Node) {
        this.leftNode = node
        this.leftNode!!.parent = this
        this.computeHash()
    }

    fun setRightNode(node: Node) {
        this.rightNode = node
        this.rightNode!!.parent = this
        if (this.leftNode != null) {
            this.computeHash()
        }
    }

    val isLeaf: Boolean
        get() = this.leftNode == null && this.rightNode == null

    fun canVerifyHash(): Boolean {
        return this.leftNode != null && this.rightNode != null || this.leftNode != null
    }

    fun verifyHash(): Boolean {
        if (this.leftNode == null && this.rightNode == null) return true
        if (this.rightNode == null) return hash.equals(leftNode!!.hash)

        if (this.leftNode == null) {
            throw InvalidParameterException("Left branch must be a node if right branch is a node!")
        }

        val leftRightHash = Hash.create(this.leftNode!!.hash, this.rightNode!!.hash)
        return hash.equals(leftRightHash)
    }
}


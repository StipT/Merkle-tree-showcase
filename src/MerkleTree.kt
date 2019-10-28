import java.security.InvalidParameterException
import java.util.ArrayList
import java.util.function.Consumer

class MerkleTree {
    lateinit var root: Node
        private set
    private val nodes: MutableList<Node>
    private val leaves: MutableList<Node>
    init {
        this.nodes = ArrayList()
        this.leaves = ArrayList()
    }

    fun appendLeaf(node: Node): Node {
        this.nodes.add(node)
        this.leaves.add(node)
        return node
    }

    fun appendLeaves(hashes: Array<Hash>): List<Node> {
        val nodes = ArrayList<Node>()
        for (hash in hashes) {
            nodes.add(this.appendLeaf(hash))
        }
        return nodes
    }
    fun buildTree(nodes: List<Node>) {
        if (nodes.isEmpty()) throw InvalidParameterException("Node list not expected to be empty!")
        if (nodes.size == 1) {
            this.root = nodes[0]
        } else {
            val parents = ArrayList<Node>()
            var i = 0
            while (i < nodes.size) {
                val right = if (i + 1 < nodes.size) nodes[i + 1] else null
                val parent = Node(nodes[i], right)
                println("New node added with the hash value of $parent")
                parents.add(parent)
                i += 2
            }
            buildTree(parents)
        }
    }
    fun buildTree(): Hash? {
        if (this.leaves.isEmpty()) throw InvalidParameterException("Cannot add to a tree with no leaves!")
        this.buildTree(this.leaves)
        return this.root.hash
    }

    fun appendLeaf(hash: Hash): Node = this.appendLeaf(Node(hash))




    fun getNodes(): List<Node> = nodes

    class ProofHash(var hash: Hash, var direction: Branch) {

        enum class Branch {
            LEFT,
            RIGHT,
            OLD_ROOT
        }

        override fun toString(): String {
            val hash = this.hash.toString()
            val direction = this.direction.toString()
            return "$hash is $direction Child"
        }
    }

    fun auditProof(leafHash: Hash): List<ProofHash> {
        val auditTrail = ArrayList<ProofHash>()

        val leafNode = this.findLeaf(leafHash)

        if (leafNode != null) {
            if (leafNode.getParent() == null) throw InvalidParameterException("Expected leaf to have a parent!")
            val parent = leafNode.getParent()
            this.buildAuditTrail(auditTrail, parent, leafNode)
        }

        return auditTrail
    }

     fun findLeaf(hash: Hash): Node? {
        return this.leaves.stream()
            .filter { leaf -> leaf.hash == hash }
            .findFirst()
            .orElse(null)
    }

    private fun buildAuditTrail(auditTrail: MutableList<ProofHash>, parent: Node?, child: Node?) {
        if (parent != null) {
            if (child!!.getParent() != parent) {
                throw InvalidParameterException("Parent of child is not expected parent!")
            }

            val nextChild = if (parent.getLeftNode() == child) parent.getRightNode() else parent.getLeftNode()
            val direction = if (parent.getLeftNode() == child)
                ProofHash.Branch.RIGHT
            else
                ProofHash.Branch.LEFT

            if (nextChild != null) auditTrail.add(ProofHash(nextChild.hash, direction))

            this.buildAuditTrail(auditTrail, parent.getParent(), child.getParent())
        }
    }



    fun appendLeaves(nodes: List<Node>) {
        for (node in nodes) {
            this.appendLeaf(node)
        }
    }

    fun addTree(tree: MerkleTree): Hash? {
        if (this.leaves.size <= 0) throw InvalidParameterException("Cannot add to a tree with no leaves!")
        tree.leaves.forEach(Consumer<Node> { this.appendLeaf(it) })
        return this.buildTree()
    }

    fun getLeaves(): List<Node> = leaves

    companion object {

        fun verifyAudit(rootHash: Hash, leafHash: Hash, auditTrail: List<ProofHash>): Boolean {
            if (auditTrail.isEmpty()) throw InvalidParameterException("Audit trail cannot be empty!")

            var testHash = leafHash

            for (auditHash in auditTrail) {
                testHash = if (auditHash.direction === ProofHash.Branch.RIGHT)
                    Hash.create(testHash, auditHash.hash)
                else
                    Hash.create(auditHash.hash, testHash)
            }

            return testHash.equals(rootHash)
        }
    }




}

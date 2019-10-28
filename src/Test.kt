import javax.naming.NoInitialContextException

fun main() {

    val dataHashA = Hash.create("A")
    val dataHashB = Hash.create("B")
    val dataHashC = Hash.create("C")
    val dataHashD = Hash.create("D")
    val dataHashE = Hash.create("E")

    val merkleTree = MerkleTree()
    merkleTree.appendLeaves(
        arrayOf(
            dataHashA,
            dataHashB,
            dataHashC,
            dataHashD,
            dataHashE
        )
    )

    println("\n ${dataHashA}")
    println("\n ${dataHashB}")
    println("\n ${dataHashC}")
    println("\n ${dataHashD}")
    println("\n ${dataHashE} \n")
    merkleTree.buildTree()
    println("\n Merkle root hash value is ${merkleTree.root}")
    val auditProof = merkleTree.auditProof(dataHashD)
    println(auditProof)

    println(MerkleTree.verifyAudit(merkleTree.root.hash, dataHashD, auditProof))
}



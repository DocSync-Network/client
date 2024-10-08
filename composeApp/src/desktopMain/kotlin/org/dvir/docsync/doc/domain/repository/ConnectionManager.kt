package org.dvir.docsync.doc.domain.repository

class ConnectionManager(
    private val docsResponsesRepository: DocsResponsesRepository
) {
    private var connectionAlive = false

    fun connect() {
        if (!connectionAlive) {
            docsResponsesRepository.connect()
            connectionAlive = true
        }
    }
}
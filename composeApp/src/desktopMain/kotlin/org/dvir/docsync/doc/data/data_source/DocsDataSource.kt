package org.dvir.docsync.doc.data.data_source

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.dvir.docsync.core.constants.Constants
import org.dvir.docsync.doc.constants.DocConstants
import org.dvir.docsync.doc.data.requests.DocAction
import org.dvir.docsync.doc.data.requests.DocListAction
import org.dvir.docsync.doc.data.responses.DocActionResponse
import org.dvir.docsync.doc.data.responses.DocListResponse
import org.dvir.docsync.doc.data.responses.DocResponse

sealed interface UserState {
    data object InMain : UserState
    data object InDocument : UserState
}

class DocsDataSource(
    private val httpClient: HttpClient,
) {
    var currentState: UserState = UserState.InMain
    private lateinit var session: WebSocketSession
    private val sessionInitialized = CompletableDeferred<Unit>()

    suspend fun connect(token: String): Flow<DocResponse> = flow {
        httpClient.webSocket(
            host = Constants.API_URL,
            port = Constants.PORT,
            path = DocConstants.ENDPOINT,
            request = {
                header("Authorization", "Bearer $token")
            }
        ) {
            session = this
            sessionInitialized.complete(Unit)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val json = frame.readText()
                        when (currentState) {
                            is UserState.InMain -> {
                                val listResponse = Json.decodeFromString<DocListResponse>(json)
                                emit(DocResponse.ListResponse(listResponse))
                            }
                            is UserState.InDocument -> {
                                val actionResponse = Json.decodeFromString<DocActionResponse>(json)
                                emit(DocResponse.ActionResponse(actionResponse))
                            }
                        }
                    }
                }
            } finally {
                close(CloseReason(CloseReason.Codes.NORMAL, "Session Closed"))
            }
        }
    }

    suspend fun sendDocListAction(action: DocListAction) {
        sessionInitialized.await()

        session.send(Frame.Text(Json.encodeToString(action)))
    }

    suspend fun sendDocAction(action: DocAction) {
        session.send(Frame.Text(Json.encodeToString(action)))
    }

    suspend fun leaveDoc() {
        sendDocAction(DocAction.LeaveDoc)
        currentState = UserState.InMain
    }
}
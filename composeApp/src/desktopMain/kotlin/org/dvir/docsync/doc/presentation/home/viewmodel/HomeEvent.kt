package org.dvir.docsync.doc.presentation.home.viewmodel

import org.dvir.docsync.doc.domain.model.ID

sealed interface HomeEvent {
    data object CreateDoc : HomeEvent
    data object OpenCreateDialog : HomeEvent
    data class DeleteDoc(val docId: ID) : HomeEvent
    data class GetDoc(val docId: ID) : HomeEvent
}
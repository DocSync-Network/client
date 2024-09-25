package org.dvir.docsync.doc.domain.cursor

interface CursorAction {
    data object Add: CursorAction
    data object Remove: CursorAction
}
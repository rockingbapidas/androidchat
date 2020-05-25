package com.bapidas.chattingapp.data.core.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

/**
 * Created by bapidas on 01/08/17.
 */
data class DataModel(
        var dataSnapshot: DataSnapshot? = null,
        var databaseError: DatabaseError? = null,
        var dataString: String = "",
        var extraString: String = ""
)
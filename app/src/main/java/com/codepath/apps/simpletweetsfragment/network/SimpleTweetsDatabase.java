package com.codepath.apps.simpletweetsfragment.network;

import com.raizlabs.android.dbflow.annotation.Database;
// @Database annotation generates a DatabaseDefinition which now references your SQLite Database
// on disk in the file named RestClientDatabase
// DBFlow supports any number of database, however individual tables and other related files can
// only be associated with one database
@Database(name = SimpleTweetsDatabase.NAME, version = SimpleTweetsDatabase.VERSION)
public class SimpleTweetsDatabase {

    public static final String NAME = "RestClientDatabase";

    public static final int VERSION = 2;
}

package br.com.alexandrenavarro.popularmovies.app.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by alexandrenavarro on 07/02/17.
 */

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {

    public static final int VERSION = 1;

    private MovieDatabase(){}

    @Table(MovieColumns.class) public static final String MOVIES = "movies";

}

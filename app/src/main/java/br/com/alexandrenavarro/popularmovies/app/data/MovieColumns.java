package br.com.alexandrenavarro.popularmovies.app.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by alexandrenavarro on 07/02/17.
 */

public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    String POSTER_PATH = "poster_path";
    @DataType(DataType.Type.TEXT) @NotNull
    String SYNOPSIS = "synopsis";
    @DataType(DataType.Type.INTEGER) @NotNull
    String RATING = "rating";
    @DataType(DataType.Type.INTEGER) @NotNull
    String RELEASE_DATE = "release_date";
    @DataType(DataType.Type.TEXT) @NotNull
    String TITLE = "title";
}

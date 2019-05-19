package com.example.acronyms;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "http://www.nactem.ac.uk/software/acromine/";

    @GET("dictionary.py")
    Observable<Response<List<LongForm>>> getLongForms(@Query("sf") String acronym);
}

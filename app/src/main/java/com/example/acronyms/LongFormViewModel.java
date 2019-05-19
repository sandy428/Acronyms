package com.example.acronyms;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LongFormViewModel extends ViewModel {

    public MutableLiveData<List<LongForm>> longFormLiveData  = new MutableLiveData<>();
    public MutableLiveData<Throwable> longFormErrorData = new MutableLiveData<>();
    public LiveData<Boolean> longFormEmptyData = Transformations.map(longFormLiveData, new Function<List<LongForm>, Boolean>() {
        @Override
        public Boolean apply(List<LongForm> input) {
            return input.isEmpty();
        }
    });
    Api api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public LongFormViewModel() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        api = retrofit.create(Api.class);
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public void loadLongFormData(String acronym) {
        Disposable disposable = api.getLongForms(acronym)
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Response<List<LongForm>>>() {
                    @Override
                    public void onNext(Response<List<LongForm>> response) {
                        if(response.code() == 200)
                            longFormLiveData.postValue(response.body());
                        else
                            longFormErrorData.postValue(new Exception());
                    }

                    @Override
                    public void onError(Throwable e) {
                        longFormErrorData.postValue(e);
                    }

                    @Override
                    public void onComplete() {}
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}


package com.example.acronyms;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Response;

import static org.junit.Assert.*;

public class ViewModelUnitTests {

    @Rule
    public InstantTaskExecutorRule testRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }

    private LongFormViewModel vm;

    @Mock Api api;
    @Mock Observer<List<LongForm>> observerLongFormLiveData;
    @Mock Observer<Boolean> observerLongFormEmptyData;
    @Mock Observer<Throwable> observerLongFormErrorData;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        vm = new LongFormViewModel();
        vm.setApi(api);
    }

    //Unit test case to test with mock data returned
    @Test
    public void testValidLongFormList() {
        final String acronym = "USB";
        List<LongForm> data = new ArrayList<>();
        List<LongFormItem> item = new ArrayList<>();
        item.add(new LongFormItem("Hello Hello Hello", 100, 100));
        data.add(new LongForm(acronym, item));

        Mockito.when(api.getLongForms(acronym)).thenReturn(Observable.just(Response.success(data)));
        vm.longFormLiveData.observeForever(observerLongFormLiveData);
        vm.loadLongFormData(acronym);
        assertEquals(1, vm.longFormLiveData.getValue().size());
    }

    //Unit test case to test empty results - mocked with empty data
    @Test
    public void testEmptyLongFormList() {
        final String acronym = "HHMYFGFH";
        List<LongForm> data = new ArrayList<>();
        /*List<LongFormItem> item = new ArrayList<>();
        item.add(new LongFormItem("Hello Hello Hello", 100, 100));
        data.add(new LongForm(acronym, item));*/

        Mockito.when(api.getLongForms(acronym)).thenReturn(Observable.just(Response.success(data)));
        vm.longFormEmptyData.observeForever(observerLongFormEmptyData);
        vm.loadLongFormData(acronym);
        assertEquals(true, vm.longFormEmptyData.getValue());
    }

    //Unit test case when there is an error in response (may be due to server error or network issue)
    @Test
    public void testErrorLongFormList() {
        final String acronym = "USB";
        ResponseBody data = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return null;
            }

            @Override
            public long contentLength() {
                return 0;
            }

            @Override
            public BufferedSource source() {
                return null;
            }
        };

        Mockito.when(api.getLongForms(acronym)).thenReturn(Observable.just(Response.error(400, data)));
        vm.longFormErrorData.observeForever(observerLongFormErrorData);
        vm.loadLongFormData(acronym);
        assertEquals(true, vm.longFormErrorData.getValue() instanceof Exception);
    }
}

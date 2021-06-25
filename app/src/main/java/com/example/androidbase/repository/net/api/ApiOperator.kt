package com.example.androidbase.repository.net.api

import com.google.gson.Gson
import com.jowney.common.net.RetrofitMaster
import com.jowney.common.util.CommonUtils
import com.jowney.common.util.rxjava.SchedulerTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody


object ApiOperator {
    //界面相关的网络操作将其disposable添加到该集合中，当退出界面时，断开连接
    private var mCompositeDisposable = CompositeDisposable()

    private fun operate(): ServerApi {
        return RetrofitMaster.getInstance().serverApi as ServerApi
    }

    fun clear() = this.mCompositeDisposable.clear()

    fun login() {
        var map: HashMap<String, String> = HashMap();
        map["pass word"] = CommonUtils.md5_32Encrypt("admin@tsl888");
        map["username"] = "admin";
        var json: String = Gson().toJson(map);
        val requestBody: RequestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val disposable = operate().login(requestBody)
            .compose(SchedulerTransformer())
            .subscribe {
                Consumer<ResponseBody> {


                }
                Consumer<Throwable> {
                }
            }
        mCompositeDisposable.add(disposable);
    }
}
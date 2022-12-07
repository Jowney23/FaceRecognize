package com.open.face.core;

/**
 * Created by Jowney on 2018/6/27.
 */

public abstract class BaseThread implements Runnable {
    private Boolean living = false;
    private Boolean waiting = false;
    private Thread thread;
    private String name;
    public BaseThread(){

    }
    public BaseThread(String name) {
        this.name = name;
        this.thread = new Thread(this, name);
    }

    /**
     * 启动线程
     */
    public void start() {
        //线程一旦结束以后 不能再start必须重新创建
        if (this.living) {
            return;
        }
        this.living = true;
        this.thread.start();
    }

    /**
     * 挂起线程
     */
    public void pause() {
        //线程已经结束 或者 正在等待 则返回
        if (!this.living || this.waiting) {
            return;
        }
        this.waiting = true;
    }

    /**
     * 恢复线程
     */
    public void resume() {
        //线程已经结束 或者 线程正在运行 则返回
        if (!this.living || !this.waiting) {
            return;
        }
        synchronized (this) {
            this.waiting = false;
            this.notifyAll();
        }
    }

    /**
     * 终止线程
     */
    public void destroy() {
        if (!this.living) {
            return;
        }
        if (this.waiting) {
            resume();
            this.waiting = false;
        }
        this.living = false;
    }

    @Override
    public void run() {
        while (this.living) {
            try {
                // 线程挂起和退出处理
                if (this.waiting) {
                    synchronized (this) {
                        this.wait();
                    }
                }
                if (!this.living) return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Do what you want to do!
            execute();
        }
    }


    protected abstract void execute();

}

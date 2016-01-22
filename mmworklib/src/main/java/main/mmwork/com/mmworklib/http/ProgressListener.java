package main.mmwork.com.mmworklib.http;

public  interface ProgressListener {
    /**
     * @param bytesRead     已下载字节数
     * @param contentLength 总字节数
     * @param done          是否下载完成
     */
    void update(long bytesRead, long contentLength, boolean done);
}

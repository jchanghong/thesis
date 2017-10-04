/**
 * The type M storage.
 * 文件前面2m保留做头信息1024*1024*2/4096=512页面
 */
class MStorage {
    public void write(long pageNumber, ByteBuffer data) throws IOException {
        FileChannel f = getChannel(pageNumber);
        int offsetInFile = (int) ((Math.abs(pageNumber) % 
		Pagesize.max_page_number) * Pagesize.page_size);
        MappedByteBuffer b = buffers.get(f);
        if (b.limit() <= offsetInFile) {
            b = addfilesize(f, offsetInFile, b);
        }
        //write into buffer
        b.position(offsetInFile);
        data.rewind();
        b.put(data);
    }
    public ByteBuffer read(long pageNumber) throws IOException {
        FileChannel f = getChannel(pageNumber);
        int offsetInFile = (int) ((Math.abs(pageNumber) % 
		Pagesize.max_page_number) * Pagesize.page_size);
        MappedByteBuffer b = buffers.get(f);
        if (b == null) { //not mapped yet
            b = f.map(FileChannel.MapMode.READ_WRITE, 0, f.size());
        }
        //增加文件大小，64m为单位
        if (b.limit() <= offsetInFile) {
            b = addfilesize(f, offsetInFile, b);
        }
        b.position(offsetInFile);
        ByteBuffer ret = b.slice();
        ret.limit(Pagesize.page_size);
        if (!transactionsDisabled || readonly) {
            // changes written into buffer will be directly written into file
            // so we need to protect buffer from modifications
            ret = ret.asReadOnlyBuffer();
        }
        return ret;
    }
}

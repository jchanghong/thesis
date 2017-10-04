/**
 * 和内存指针差不多，new 后得到地址,这里地址是page index
 * 每个页面开始分别是type，记录大小，数据，页面后2个字节用来连接每个页面
 * 一个页面pagesize-2-4-4
 */
public class DiscIO implements MdiscIO {
    @Override
    public int write(Object o) {
        byte[] bytes = ObjectSeriaer.getbytes(o);
        int[] pages = pagemanager.getfreepanages(bytes.length);
        if (pages.length == 1) {
            try {
                ByteBuffer buffer = storage.read(pages[0]);
                if (o instanceof DHtree)
                    buffer.putShort(Pagesize.pagehead_tree);
                else if (o instanceof DHtreeNode) {
                    buffer.putShort(Pagesize.pagehead_node);
                } else {
                    buffer.putShort(Pagesize.pagehead_other);
                }
                buffer.putInt(bytes.length);
                buffer.put(bytes);
                storage.write(pages[0], buffer);
                ObjectMap.putorupdate(o, pages[0]);
                return pages[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return writemorepage(bytes, pages, o);
        }
        return 0;
    }
    @Override
    public int update(Object o, int recid) {
        if (!ObjectMap.map.containsKey(recid)) {
            return -1;
        }
        byte[] bytes = ObjectSeriaer.getbytes(o);
        if (bytes.length <= Pagesize.page_size_for_content) {
            try {
                ByteBuffer buffer = storage.read(recid);
                if (o instanceof DHtree)
                    buffer.putShort(Pagesize.pagehead_tree);
                else if (o instanceof DHtreeNode) {
                    buffer.putShort(Pagesize.pagehead_node);
                } else {
                    buffer.putShort(Pagesize.pagehead_other);
                }
                buffer.putInt(bytes.length);
                buffer.put(bytes);
                storage.write(recid, buffer);
                ObjectMap.putorupdate(o, recid);
                return recid;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            updatemoredata(bytes, recid, o);
            return recid;
        }
    }
}

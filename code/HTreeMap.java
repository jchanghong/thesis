public class HTreeMap implements Map<String, Object> ,Serializable{
    @Override
    public Object get(Object key) {
        if (key == null) {
            throw new NullPointerException("key not null");
        }
        int hashcode = Math.abs(key.hashCode());
        HtreeNode htreeNode = root;
        while (htreeNode != null) {
            LOGGER.debug("this node is:" + htreeNode.key);
            if (htreeNode.hasV && key.equals(htreeNode.key)) {
                LOGGER.debug("find " + key);
                return htreeNode.values;
            }
            int hashindex = hashcode % htreeNode.hashtable_size;
            htreeNode = htreeNode.childs[hashindex];
        }
        return null;
    }
    @Override
    public Object put(String key, Object value) {
        if (key == null || value == null) {
            throw new NullPointerException("key not null");
        }
        int hashcode = Math.abs(key.hashCode());
        HtreeNode htreeNode = root;
        HtreeNode pre = null;
        while (htreeNode != null) {
            if (htreeNode.hasV && key.equals(htreeNode.key)) {
                Object stemp = htreeNode.values;
                htreeNode.values = value;
                return stemp;
            } else if (!htreeNode.hasV) {
                htreeNode.hasV = true;
                htreeNode.values = value;
                htreeNode.key = key;
                return null;
            }
            pre = htreeNode;
            int hashindex = hashcode % htreeNode.hashtable_size;
            htreeNode = htreeNode.childs[hashindex];
        }
        int hashindex = hashcode % pre.hashtable_size;
        pre.childs[hashindex] = new HtreeNode(pre.high + 1, key, value);
        allnodes.add(pre.childs[hashindex]);
            return null;
    }
    @Override
    public Object remove(Object key) {
        if (key == null) {
            throw new NullPointerException("key not null");
        }
        int hashcode = Math.abs(key.hashCode());
        HtreeNode htreeNode = root;
        while (htreeNode != null) {
            if (htreeNode.hasV && key.equals(htreeNode.key)) {
                htreeNode.hasV = false;
                return htreeNode.values;
            }
            int hashindex = hashcode % htreeNode.hashtable_size;
            htreeNode = htreeNode.childs[hashindex];
        }
        return null;
    }
}

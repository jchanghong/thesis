public class HtreeNode {
    public int high;//root is 0
    public int hashtable_size;
    public HtreeNode[] childs;
    public boolean hasV;
    public String key;
    public Object values;
    public HtreeNode(int high, String key, Object values) {
        this.high = high;
        hashtable_size = HashCodes.codes[high];
        this.key = key;
        this.values = values;
        childs = new HtreeNode[hashtable_size];
        hasV = key != null;
    }
}

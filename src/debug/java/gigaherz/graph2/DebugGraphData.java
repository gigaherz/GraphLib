package gigaherz.graph2;

public class DebugGraphData implements Mergeable<DebugGraphData>
{
    private static int sUid = 0;

    private final int uid;

    public DebugGraphData()
    {
        uid = ++sUid;
    }

    public DebugGraphData(int uid)
    {
        this.uid = uid;
    }

    @Override
    public DebugGraphData mergeWith(DebugGraphData other)
    {
        return new DebugGraphData(uid + other.uid);
    }

    @Override
    public DebugGraphData copy()
    {
        return new DebugGraphData();
    }

    public int getUid()
    {
        return uid;
    }
}

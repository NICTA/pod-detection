package au.com.nicta.ssrg.pod.cfmchecker.newcore;

public class LinkState {
    public LinkState(Link link,
                     int tokenProduced,
                     int tokenConsumed,
                     int tokenMissing) {
        this.link = link;
        this.tokenProduced = tokenProduced;
        this.tokenConsumed = tokenConsumed;
        this.tokenMissing = tokenMissing;
    }

    public Link getLink() { return link; }
    public int getTokenProduced() { return tokenProduced; }
    public int getTokenConsumed() { return tokenConsumed; }
    public int getTokenMissing() { return tokenMissing; }

    public int getTokenRemaining() {
        return tokenProduced + tokenMissing - tokenConsumed;
    }

    public boolean hasTokenRemaining() {
        return getTokenRemaining() > 0;
    }

    public LinkState produceToken() {
        return new LinkState(link, tokenProduced + 1, tokenConsumed, tokenMissing);
    }

    public LinkState consumeToken() {
        int missing = hasTokenRemaining() ? tokenMissing : tokenMissing + 1;
        return new LinkState(link, tokenProduced, tokenConsumed + 1, missing);
    }

    private Link link;
    private int tokenProduced;
    private int tokenConsumed;
    private int tokenMissing;
}

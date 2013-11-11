package loa;

/** Indicates a piece or player color.
    @author Nick Holt*/
enum Side {
    /** The names of the two sides, with a "Buffer" player
     *  who owns the buffer pieces (which do not move). */
    BLACK, WHITE, BUFFER;

    /** Return the opposing color. Buffer player is ignored. */
    Side opponent() {
        return this == BLACK ? WHITE : BLACK;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}

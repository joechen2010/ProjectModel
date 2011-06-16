package com.joe.utilities.core.configuration.database;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author rrichard
 *
 */
public final class Sequence {

    private static long currTime = System.currentTimeMillis();
    private static long currSeq = 1;
    private String prefix;
    private long value;
    
    /**
     * 
     */
    public Sequence() {
        prefix = null;
        value = nextNumber();
    }
    
    /**
     * 
     */
    public Sequence(String prefix) {
        this.prefix = prefix;
        value = nextNumber();
   }
    
    
    /**
     * @return
     */
    public String toStringWprefix() {
        StringBuffer returnValue = new StringBuffer();
        if (this.prefix != null) {
            returnValue.append(prefix);
            returnValue.append("-");
        }
        returnValue.append(value);

        return returnValue.toString();
    }
    
    
    /**
     * @return
     */
    public String toHexStringWprefix() {
        StringBuffer returnValue = new StringBuffer();
        if (this.prefix != null) {
            returnValue.append(prefix);
            returnValue.append("-");
        }
        returnValue.append(toHexString());

        return returnValue.toString();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Long.toString(value);
    }
    
    /**
     * @return
     */
    public String toHexString() {
        return Long.toHexString(value);
    }
    
    public long longValue() {
        return value;
    }
    
   /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                    .append(value)
                    .toHashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Not strictly necessary, but often a good optimization
        if (this == obj)
            return true;

        if (!(obj instanceof Sequence))
            return false;

        Sequence other = (Sequence) obj;
        
        return new EqualsBuilder()
                    .append(value, other.value)
                    .isEquals();
    }
    
    public Sequence nextValue() {
        this.value = nextNumber();
        return this;
    }
    /**
     * @return
     */
    private static synchronized long nextNumber() {
        long newTimMillis = System.currentTimeMillis();
        if (currTime != newTimMillis) {
            currTime = newTimMillis;
            currSeq = 1;
        }
        long returnValue = Long.parseLong(String.format("%1$d%2$d", currTime, currSeq++));
        return returnValue;
    }

}

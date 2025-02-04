/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.1.0
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.equeim.libtremotesf;

public class Tracker {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected Tracker(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Tracker obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        libtremotesfJNI.delete_Tracker(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int id() {
    return libtremotesfJNI.Tracker_id(swigCPtr, this);
  }

  public String announce() {
    return libtremotesfJNI.Tracker_announce(swigCPtr, this);
}

  static public class AnnounceHostInfo {
    private transient long swigCPtr;
    protected transient boolean swigCMemOwn;
  
    protected AnnounceHostInfo(long cPtr, boolean cMemoryOwn) {
      swigCMemOwn = cMemoryOwn;
      swigCPtr = cPtr;
    }
  
    protected static long getCPtr(AnnounceHostInfo obj) {
      return (obj == null) ? 0 : obj.swigCPtr;
    }
  
    @SuppressWarnings("deprecation")
    protected void finalize() {
      delete();
    }
  
    public synchronized void delete() {
      if (swigCPtr != 0) {
        if (swigCMemOwn) {
          swigCMemOwn = false;
          libtremotesfJNI.delete_Tracker_AnnounceHostInfo(swigCPtr);
        }
        swigCPtr = 0;
      }
    }
  
    public String getHost() {
      return libtremotesfJNI.Tracker_AnnounceHostInfo_host_get(swigCPtr, this);
  }
  
    public boolean getIsIpAddress() {
      return libtremotesfJNI.Tracker_AnnounceHostInfo_isIpAddress_get(swigCPtr, this);
    }
  
    public AnnounceHostInfo() {
      this(libtremotesfJNI.new_Tracker_AnnounceHostInfo(), true);
    }
  
  }

  public Tracker.AnnounceHostInfo announceHostInfo() {
    return new Tracker.AnnounceHostInfo(libtremotesfJNI.Tracker_announceHostInfo(swigCPtr, this), true);
  }

  public Tracker.Status status() {
    return Tracker.Status.swigToEnum(libtremotesfJNI.Tracker_status(swigCPtr, this));
  }

  public String errorMessage() {
    return libtremotesfJNI.Tracker_errorMessage(swigCPtr, this);
}

  public int peers() {
    return libtremotesfJNI.Tracker_peers(swigCPtr, this);
  }

  public long nextUpdateTime() {
    return libtremotesfJNI.Tracker_nextUpdateTime(swigCPtr, this);
  }

  public int nextUpdateEta() {
    return libtremotesfJNI.Tracker_nextUpdateEta(swigCPtr, this);
  }

  public enum Status {
    Inactive,
    Active,
    Queued,
    Updating,
    Error;

    public final int swigValue() {
      return swigValue;
    }

    public static Status swigToEnum(int swigValue) {
      Status[] swigValues = Status.class.getEnumConstants();
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (Status swigEnum : swigValues)
        if (swigEnum.swigValue == swigValue)
          return swigEnum;
      throw new IllegalArgumentException("No enum " + Status.class + " with value " + swigValue);
    }

    @SuppressWarnings("unused")
    private Status() {
      this.swigValue = SwigNext.next++;
    }

    @SuppressWarnings("unused")
    private Status(int swigValue) {
      this.swigValue = swigValue;
      SwigNext.next = swigValue+1;
    }

    @SuppressWarnings("unused")
    private Status(Status swigEnum) {
      this.swigValue = swigEnum.swigValue;
      SwigNext.next = this.swigValue+1;
    }

    private final int swigValue;

    private static class SwigNext {
      private static int next = 0;
    }
  }

}

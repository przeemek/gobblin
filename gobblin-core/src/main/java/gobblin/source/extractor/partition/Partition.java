package gobblin.source.extractor.partition;

import gobblin.configuration.ConfigurationKeys;
import gobblin.source.Source;
import gobblin.source.extractor.WatermarkInterval;
import gobblin.source.extractor.extract.LongWatermark;
import gobblin.source.workunit.WorkUnit;

import lombok.EqualsAndHashCode;
import lombok.Getter;


/**
 * This class encapsulates the two ends, {@link #lowWatermark} and {@link #highWatermark}, of a partition and some
 * metadata, e.g. {@link #hasUserSpecifiedHighWatermark}, to describe the partition.
 *
 * <p>
 *   A {@link Source} partitions its data into a collection of {@link Partition}, each of which will be used to create
 *   a {@link WorkUnit}.
 *
 *   Currently, the {@link #lowWatermark} of a partition in Gobblin is inclusive and its {@link #highWatermark} is
 *   exclusive unless it is the last partition.
 * </p>
 *
 * @author zhchen
 */
@EqualsAndHashCode
public class Partition {
  public static final String IS_LAST_PARTIITON = "partition.isLastPartition";
  public static final String HAS_USER_SPECIFIED_HIGH_WATERMARK = "partition.hasUserSpecifiedHighWatermark";

  @Getter
  private final long lowWatermark;
  @Getter
  private final boolean isLowWatermarkInclusive;
  @Getter
  private final long highWatermark;
  @Getter
  private final boolean isHighWatermarkInclusive;
  @Getter
  private final boolean isLastPartition;

  /**
   * Indicate if the Partition highWatermark is set as user specifies, not computed on the fly
   */
  private final boolean hasUserSpecifiedHighWatermark;

  public Partition(long lowWatermark, long highWatermark, boolean isLastPartition,
      boolean hasUserSpecifiedHighWatermark) {
    this.lowWatermark = lowWatermark;
    this.highWatermark = highWatermark;

    this.isLowWatermarkInclusive = true;
    this.isHighWatermarkInclusive = isLastPartition;

    this.isLastPartition = isLastPartition;
    this.hasUserSpecifiedHighWatermark = hasUserSpecifiedHighWatermark;
  }

  public Partition(long lowWatermark, long highWatermark, boolean hasUserSpecifiedHighWatermark) {
    this(lowWatermark, highWatermark, false, hasUserSpecifiedHighWatermark);
  }

  public Partition(long lowWatermark, long highWatermark) {
    this(lowWatermark, highWatermark, false);
  }

  public boolean getHasUserSpecifiedHighWatermark() {
    return hasUserSpecifiedHighWatermark;
  }

  public void serialize(WorkUnit workUnit) {
    workUnit.setWatermarkInterval(
        new WatermarkInterval(new LongWatermark(lowWatermark), new LongWatermark(highWatermark)));
    if (hasUserSpecifiedHighWatermark) {
      workUnit.setProp(Partition.HAS_USER_SPECIFIED_HIGH_WATERMARK, true);
    }
    if (isLastPartition) {
      workUnit.setProp(Partition.IS_LAST_PARTIITON, true);
    }
  }

  public static Partition deserialize(WorkUnit workUnit) {
    long lowWatermark = ConfigurationKeys.DEFAULT_WATERMARK_VALUE;
    long highWatermark = ConfigurationKeys.DEFAULT_WATERMARK_VALUE;

    if (workUnit.getProp(ConfigurationKeys.WATERMARK_INTERVAL_VALUE_KEY) != null) {
      lowWatermark = workUnit.getLowWatermark(LongWatermark.class).getValue();
      highWatermark = workUnit.getExpectedHighWatermark(LongWatermark.class).getValue();
    }

    return new Partition(lowWatermark, highWatermark, workUnit.getPropAsBoolean(Partition.IS_LAST_PARTIITON),
        workUnit.getPropAsBoolean(Partition.HAS_USER_SPECIFIED_HIGH_WATERMARK));
  }
}

package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 */
public class BlockWorkerTest {

    private static final Random RANDOM = new Random();
    private static final BlockState AIR = BlockTypes.AIR.getDefaultState();

    @Test
    public void testFill() {
        final MutableBlockVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final Vector3i min = volume.getBlockMin();
        final Vector3i max = volume.getBlockMax();
        // Fill the reference with random blocks using regular iteration
        final MutableBlockVolume reference = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    reference.setBlock(x, y, z, BlockBufferTest.getRandomBlock());
                }
            }
        }
        // Use the fill function to copy the reference
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker = volume.getBlockWorker();
        worker.fill(reference::getBlock);
        // Check if volume and reference are the same
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Assert.assertNotEquals(AIR, volume.getBlock(x, y, z));
                    Assert.assertEquals(reference.getBlock(x, y, z), volume.getBlock(x, y, z));
                }
            }
        }
    }

    @Test
    public void testMap() {
        final MutableBlockVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final Vector3i min = volume.getBlockMin();
        final Vector3i max = volume.getBlockMax();
        // Fill the reference with either air or a random block
        final MutableBlockVolume reference = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker = reference.getBlockWorker();
        worker.fill((x, y, z) -> RANDOM.nextBoolean() ? AIR : BlockBufferTest.getRandomBlock());
        // Map air to a random block and anything else to air into a new volume
        worker.map(((v, x, y, z) -> v.getBlock(x, y, z).equals(AIR) ? BlockBufferTest.getRandomBlock() : AIR), volume);
        // Check if volume and reference follow the mapping rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    if (reference.getBlock(x, y, z).equals(AIR)) {
                        Assert.assertNotEquals(AIR, volume.getBlock(x, y, z));
                    } else {
                        Assert.assertEquals(AIR, volume.getBlock(x, y, z));
                    }
                }
            }
        }
    }

    @Test
    public void testMerge() {
        final MutableBlockVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final Vector3i min = volume.getBlockMin();
        final Vector3i max = volume.getBlockMax();
        // Fill two references with either air or a random block (also test with different sized volumes)
        final MutableBlockVolume reference1 = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker1 = reference1.getBlockWorker();
        worker1.fill((x, y, z) -> RANDOM.nextBoolean() ? AIR : BlockBufferTest.getRandomBlock());
        final MutableBlockVolume reference2 = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(22, 16, 18);
        final MutableBlockVolume shiftedReference2 = reference2.getBlockView(DiscreteTransform3.fromTranslation(-42, 16, 71));
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker2 = reference1.getBlockWorker();
        worker2.fill((x, y, z) -> RANDOM.nextBoolean() ? AIR : BlockBufferTest.getRandomBlock());
        // Merge by using the non-air if one of the two blocks isn't air or using the first for any other case
        worker1.merge(shiftedReference2, (firstVolume, xFirst, yFirst, zFirst, secondVolume, xSecond, ySecond, zSecond) -> {
                final BlockState firstBlock = firstVolume.getBlock(xFirst, yFirst, zFirst);
                final BlockState secondBlock = secondVolume.getBlock(xSecond, ySecond, zSecond);
                if (firstBlock.equals(AIR) && !secondBlock.equals(AIR)) {
                    return secondBlock;
                }
                return firstBlock;
            },
            volume);
        // Check if volume and references follow the merging rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockState block = volume.getBlock(x, y, z);
                    if (block.equals(AIR)) {
                        Assert.assertEquals(AIR, reference1.getBlock(x, y, z));
                        Assert.assertEquals(AIR, reference2.getBlock(x, y, z));
                    } else if (reference1.getBlock(x, y, z).equals(AIR)) {
                        Assert.assertEquals(reference2.getBlock(x, y, z), block);
                    } else {
                        Assert.assertEquals(reference1.getBlock(x, y, z), block);
                    }
                }
            }
        }
    }

    @Test
    public void testReduce() {
        final MutableBlockVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final Vector3i min = volume.getBlockMin();
        final Vector3i max = volume.getBlockMax();
        // Fill the volume with either air or a random block and hash the coordinate of air blocks
        int airHash = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockState block;
                    if (RANDOM.nextBoolean()) {
                        block = AIR;
                        airHash += x | y | z;
                    } else {
                        block = BlockBufferTest.getRandomBlock();
                    }
                    volume.setBlock(x, y, z, block);
                }
            }
        }
        // Reduce by hashing the coordinates of air blocks
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker = volume.getBlockWorker();
        final int reduction = worker.reduce((v, x, y, z, r) -> r + (v.getBlock(x, y, z).equals(AIR) ? x | y | z : 0), (a, b) -> a + b, 0);
        Assert.assertEquals(airHash, reduction);
    }

    @Test
    public void testIterate() {
        final MutableBlockVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 10, 15);
        final Vector3i min = volume.getBlockMin();
        final Vector3i max = volume.getBlockMax();
        // Fill the volume with either air or a random block and add the coordinate of air blocks
        final Set<Vector3i> airCoordinates = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockState block;
                    if (RANDOM.nextBoolean()) {
                        block = AIR;
                        airCoordinates.add(new Vector3i(x, y, z));
                    } else {
                        block = BlockBufferTest.getRandomBlock();
                    }
                    volume.setBlock(x, y, z, block);
                }
            }
        }
        // Iterate and add the coordinates of air blocks
        final MutableBlockVolumeWorker<? extends MutableBlockVolume> worker = volume.getBlockWorker();
        final Set<Vector3i> coordinates = new HashSet<>();
        worker.iterate((v, x, y, z) -> {
            if (v.getBlock(x, y, z).equals(AIR)) {
                coordinates.add(new Vector3i(x, y, z));
            }
        });
        Assert.assertEquals(airCoordinates, coordinates);
    }

}

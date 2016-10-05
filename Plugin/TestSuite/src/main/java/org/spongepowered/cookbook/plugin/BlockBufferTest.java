package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

import java.util.Random;

public class BlockBufferTest {

    private static final Random RANDOM = new Random();
    private static final BlockState[] TEST_BLOCKS = {
        BlockTypes.STONE.getDefaultState(),
        BlockTypes.DIRT.getDefaultState(),
        BlockTypes.GRASS.getDefaultState(),
        BlockTypes.LOG.getDefaultState(),
        BlockTypes.WOOL.getDefaultState(),
        BlockTypes.COBBLESTONE.getDefaultState(),
        BlockTypes.COAL_ORE.getDefaultState(),
        BlockTypes.IRON_ORE.getDefaultState(),
        BlockTypes.GOLD_ORE.getDefaultState(),
        BlockTypes.DIAMOND_ORE.getDefaultState()
    };

    @Test
    public void testBlockBuffer() {
        // Test regular buffer
        final MutableBlockVolume buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(20, 15, 25);
        Assert.assertEquals(Vector3i.ZERO, buffer.getBlockMin());
        Assert.assertEquals(new Vector3i(19, 14, 24), buffer.getBlockMax());
        Assert.assertEquals(new Vector3i(20, 15, 25), buffer.getBlockSize());
        testBuffer(buffer);
        // Test unmodifiable view
        final UnmodifiableBlockVolume unmodifiable = buffer.getUnmodifiableBlockView();
        Assert.assertEquals(Vector3i.ZERO, unmodifiable.getBlockMin());
        Assert.assertEquals(new Vector3i(19, 14, 24), unmodifiable.getBlockMax());
        Assert.assertEquals(new Vector3i(20, 15, 25), unmodifiable.getBlockSize());
        testBuffer(unmodifiable);
        // Test immutable copy
        final ImmutableBlockVolume copy = buffer.getImmutableBlockCopy();
        Assert.assertEquals(Vector3i.ZERO, copy.getBlockMin());
        Assert.assertEquals(new Vector3i(19, 14, 24), copy.getBlockMax());
        Assert.assertEquals(new Vector3i(20, 15, 25), copy.getBlockSize());
        testBuffer(copy);
        // Test downsize views
        final MutableBlockVolume downsize = buffer.getBlockView(new Vector3i(4, 3, 5), new Vector3i(15, 11, 20));
        Assert.assertEquals(new Vector3i(4, 3, 5), downsize.getBlockMin());
        Assert.assertEquals(new Vector3i(15, 11, 20), downsize.getBlockMax());
        Assert.assertEquals(new Vector3i(12, 9, 16), downsize.getBlockSize());
        testBuffer(downsize);
        final UnmodifiableBlockVolume unmodifiableDownsize = unmodifiable.getBlockView(new Vector3i(4, 3, 5), new Vector3i(15, 11, 20));
        Assert.assertEquals(new Vector3i(4, 3, 5), unmodifiableDownsize.getBlockMin());
        Assert.assertEquals(new Vector3i(15, 11, 20), unmodifiableDownsize.getBlockMax());
        Assert.assertEquals(new Vector3i(12, 9, 16), unmodifiableDownsize.getBlockSize());
        testBuffer(unmodifiableDownsize);
        final ImmutableBlockVolume immutableDownsize = copy.getBlockView(new Vector3i(4, 3, 5), new Vector3i(15, 11, 20));
        Assert.assertEquals(new Vector3i(4, 3, 5), immutableDownsize.getBlockMin());
        Assert.assertEquals(new Vector3i(15, 11, 20), immutableDownsize.getBlockMax());
        Assert.assertEquals(new Vector3i(12, 9, 16), immutableDownsize.getBlockSize());
        testBuffer(immutableDownsize);
        // Test relative view
        final MutableBlockVolume relative = downsize.getRelativeBlockView();
        Assert.assertEquals(Vector3i.ZERO, relative.getBlockMin());
        Assert.assertEquals(new Vector3i(11, 8, 15), relative.getBlockMax());
        Assert.assertEquals(new Vector3i(12, 9, 16), relative.getBlockSize());
        testBuffer(relative);
    }

    @Test
    public void testBlockBufferRotate() {
        final MutableBlockVolume buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBlockBuffer(3, 2, 1);
        buffer.setBlock(0, 0, 0, TEST_BLOCKS[0], null);
        buffer.setBlock(1, 0, 0, TEST_BLOCKS[1], null);
        buffer.setBlock(2, 0, 0, TEST_BLOCKS[2], null);
        buffer.setBlock(0, 1, 0, TEST_BLOCKS[3], null);
        buffer.setBlock(1, 1, 0, TEST_BLOCKS[4], null);
        buffer.setBlock(2, 1, 0, TEST_BLOCKS[5], null);
        // 90 degrees
        final DiscreteTransform3 _90degrees = DiscreteTransform3.rotationAroundCenter(1, Axis.Y, buffer.getBlockSize());
        MutableBlockVolume rotated = buffer.getBlockView(_90degrees);
        Assert.assertEquals(TEST_BLOCKS[0], rotated.getBlock(1, 0, -1));
        Assert.assertEquals(TEST_BLOCKS[1], rotated.getBlock(1, 0, 0));
        Assert.assertEquals(TEST_BLOCKS[2], rotated.getBlock(1, 0, 1));
        Assert.assertEquals(TEST_BLOCKS[3], rotated.getBlock(1, 1, -1));
        Assert.assertEquals(TEST_BLOCKS[4], rotated.getBlock(1, 1, 0));
        Assert.assertEquals(TEST_BLOCKS[5], rotated.getBlock(1, 1, 1));
        // 180 degrees
        rotated = rotated.getBlockView(_90degrees);
        Assert.assertEquals(TEST_BLOCKS[0], rotated.getBlock(2, 0, 0));
        Assert.assertEquals(TEST_BLOCKS[1], rotated.getBlock(1, 0, 0));
        Assert.assertEquals(TEST_BLOCKS[2], rotated.getBlock(0, 0, 0));
        Assert.assertEquals(TEST_BLOCKS[3], rotated.getBlock(2, 1, 0));
        Assert.assertEquals(TEST_BLOCKS[4], rotated.getBlock(1, 1, 0));
        Assert.assertEquals(TEST_BLOCKS[5], rotated.getBlock(0, 1, 0));
        // 270 degrees
        rotated = rotated.getBlockView(_90degrees);
        Assert.assertEquals(TEST_BLOCKS[0], rotated.getBlock(1, 0, 1));
        Assert.assertEquals(TEST_BLOCKS[1], rotated.getBlock(1, 0, 0));
        Assert.assertEquals(TEST_BLOCKS[2], rotated.getBlock(1, 0, -1));
        Assert.assertEquals(TEST_BLOCKS[3], rotated.getBlock(1, 1, 1));
        Assert.assertEquals(TEST_BLOCKS[4], rotated.getBlock(1, 1, 0));
        Assert.assertEquals(TEST_BLOCKS[5], rotated.getBlock(1, 1, -1));
    }

    private void testBuffer(BlockVolume buffer) {
        final Vector3i min = buffer.getBlockMin();
        final Vector3i max = buffer.getBlockMax();
        // Test bound validation
        try {
            buffer.getBlock(min.sub(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.getBlock(max.add(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Extra tests for mutable buffers
        if (buffer instanceof MutableBlockVolume) {
            // Also fills the buffer with random data
            testMutableBuffer((MutableBlockVolume) buffer);
        }
        // Test data copy
        final MutableBlockVolume copy = buffer.getBlockCopy();
        Assert.assertEquals(min, copy.getBlockMin());
        Assert.assertEquals(max, copy.getBlockMax());
        Assert.assertEquals(buffer.getBlockSize(), copy.getBlockSize());
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockState bufferBlock = buffer.getBlock(x, y, z);
                    final BlockState copyBlock = copy.getBlock(x, y, z);
                    Assert.assertNotNull(copyBlock);
                    Assert.assertNotNull(bufferBlock);
                    Assert.assertEquals(bufferBlock, copyBlock);
                }
            }
        }
        // Test distinctiveness of copies
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    copy.setBlock(x, y, z, BlockTypes.REDSTONE_ORE.getDefaultState(), null);
                }
            }
        }
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Assert.assertNotEquals(BlockTypes.REDSTONE_ORE.getDefaultState(), buffer.getBlock(x, y, z));
                }
            }
        }
    }

    private void testMutableBuffer(MutableBlockVolume buffer) {
        final Vector3i min = buffer.getBlockMin();
        final Vector3i max = buffer.getBlockMax();
        // Test bound validation
        try {
            buffer.getBlock(min.sub(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.getBlock(max.add(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Test fill
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    final BlockState randomBlock = getRandomBlock();
                    buffer.setBlock(x, y, z, randomBlock, null);
                    Assert.assertEquals(randomBlock, buffer.getBlock(x, y, z));
                }
            }
        }
    }

    public static BlockState getRandomBlock() {
        return TEST_BLOCKS[RANDOM.nextInt(TEST_BLOCKS.length)];
    }

}

package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;

import java.util.Random;

public class BiomeBufferTest {

    private static final Random RANDOM = new Random();
    private static final BiomeType[] TEST_BIOMES = {
            BiomeTypes.OCEAN,
            BiomeTypes.BEACH,
            BiomeTypes.FOREST,
            BiomeTypes.PLAINS,
            BiomeTypes.TAIGA,
            BiomeTypes.JUNGLE,
            BiomeTypes.DESERT,
            BiomeTypes.HELL,
            BiomeTypes.MESA,
            BiomeTypes.SWAMPLAND
    };

    @Test
    public void testBiomeBuffer() {
        // Test regular buffer
        final MutableBiomeVolume buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        Assert.assertEquals(Vector3i.ZERO, buffer.getBiomeMin());
        Assert.assertEquals(new Vector3i(19, 0, 14), buffer.getBiomeMax());
        Assert.assertEquals(new Vector3i(20, 1, 15), buffer.getBiomeSize());
        testBuffer(buffer);
        // Test unmodifiable view
        final UnmodifiableBiomeVolume unmodifiable = buffer.getUnmodifiableBiomeView();
        Assert.assertEquals(Vector3i.ZERO, unmodifiable.getBiomeMin());
        Assert.assertEquals(new Vector3i(19, 0, 14), unmodifiable.getBiomeMax());
        Assert.assertEquals(new Vector3i(20, 1, 15), unmodifiable.getBiomeSize());
        testBuffer(unmodifiable);
        // Test immutable copy
        final ImmutableBiomeVolume copy = buffer.getImmutableBiomeCopy();
        Assert.assertEquals(Vector3i.ZERO, copy.getBiomeMin());
        Assert.assertEquals(new Vector3i(19, 0, 14), copy.getBiomeMax());
        Assert.assertEquals(new Vector3i(20, 1, 15), copy.getBiomeSize());
        testBuffer(copy);
        // Test downsize views
        final MutableBiomeVolume downsize = buffer.getBiomeView(new Vector3i(4, 0, 3), new Vector3i(15, 0, 11));
        Assert.assertEquals(new Vector3i(4, 0, 3), downsize.getBiomeMin());
        Assert.assertEquals(new Vector3i(15, 0, 11), downsize.getBiomeMax());
        Assert.assertEquals(new Vector3i(12, 1, 9), downsize.getBiomeSize());
        testBuffer(downsize);
        final UnmodifiableBiomeVolume unmodifiableDownsize = unmodifiable.getBiomeView(new Vector3i(4, 0, 3), new Vector3i(15, 0, 11));
        Assert.assertEquals(new Vector3i(4, 0, 3), unmodifiableDownsize.getBiomeMin());
        Assert.assertEquals(new Vector3i(15, 0, 11), unmodifiableDownsize.getBiomeMax());
        Assert.assertEquals(new Vector3i(12, 1, 9), unmodifiableDownsize.getBiomeSize());
        testBuffer(unmodifiableDownsize);
        final ImmutableBiomeVolume immutableDownsize = copy.getBiomeView(new Vector3i(4, 0, 3), new Vector3i(15, 0, 11));
        Assert.assertEquals(new Vector3i(4, 0, 3), immutableDownsize.getBiomeMin());
        Assert.assertEquals(new Vector3i(15, 0, 11), immutableDownsize.getBiomeMax());
        Assert.assertEquals(new Vector3i(12, 1, 9), immutableDownsize.getBiomeSize());
        testBuffer(immutableDownsize);
        // Test relative view
        final MutableBiomeVolume relative = downsize.getRelativeBiomeView();
        Assert.assertEquals(Vector3i.ZERO, relative.getBiomeMin());
        Assert.assertEquals(new Vector3i(11, 0, 8), relative.getBiomeMax());
        Assert.assertEquals(new Vector3i(12, 1, 9), relative.getBiomeSize());
        testBuffer(relative);
    }

    @Test
    public void testBiomeBufferRotate() {
        final MutableBiomeVolume buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(2, 1, 2);
        buffer.setBiome(0, 0, 0, TEST_BIOMES[0]);
        buffer.setBiome(1, 0, 0, TEST_BIOMES[1]);
        buffer.setBiome(0, 0, 1, TEST_BIOMES[2]);
        buffer.setBiome(1, 0, 1, TEST_BIOMES[3]);
        // 90 degrees
        final DiscreteTransform3 _90degrees = DiscreteTransform3.rotationAroundCenter(1, Axis.Y, buffer.getBiomeSize());
        MutableBiomeVolume rotated = buffer.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(0, 0, 0));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(1, 0, 0));
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(0, 0, 1));
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(1, 0, 1));
        // 180 degrees
        rotated = rotated.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(0, 0, 0));
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(1, 0, 0));
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(0, 0, 1));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(1, 0, 1));
        // 270 degrees
        rotated = rotated.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(0, 0, 0));
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(1, 0, 0));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(0, 0, 1));
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(1, 0, 1));
    }

    private void testBuffer(BiomeVolume buffer) {
        final Vector3i min = buffer.getBiomeMin();
        final Vector3i max = buffer.getBiomeMax();
        // Test bound validation
        try {
            buffer.getBiome(min.sub(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.getBiome(max.add(1, 0, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Extra tests for mutable buffers
        if (buffer instanceof MutableBiomeVolume) {
            // Also fills the buffer with random data
            testMutableBuffer((MutableBiomeVolume) buffer);
        }
        // Test data copy
        final MutableBiomeVolume copy = buffer.getBiomeCopy();
        Assert.assertEquals(min, copy.getBiomeMin());
        Assert.assertEquals(max, copy.getBiomeMax());
        Assert.assertEquals(buffer.getBiomeSize(), copy.getBiomeSize());
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                final BiomeType bufferBiome = buffer.getBiome(x, 0, z);
                final BiomeType copyBiome = copy.getBiome(x, 0, z);
                Assert.assertNotNull(copyBiome);
                Assert.assertNotNull(bufferBiome);
                Assert.assertEquals(bufferBiome, copyBiome);
            }
        }
        // Test distinctiveness of copies
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                copy.setBiome(x, 0, z, BiomeTypes.SKY);
            }
        }
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                Assert.assertNotEquals(BiomeTypes.SKY, buffer.getBiome(x, 0, z));
            }
        }
    }

    private void testMutableBuffer(MutableBiomeVolume buffer) {
        final Vector3i min = buffer.getBiomeMin();
        final Vector3i max = buffer.getBiomeMax();
        // Test bound validation
        try {
            buffer.setBiome(min.sub(1, 0, 0), BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.setBiome(max.add(1, 0, 0), BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Test fill
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                final BiomeType randomBiome = getRandomBiome();
                buffer.setBiome(x, 0, z, randomBiome);
                Assert.assertEquals(randomBiome, buffer.getBiome(x, 0, z));
            }
        }
    }

    public static BiomeType getRandomBiome() {
        return TEST_BIOMES[RANDOM.nextInt(TEST_BIOMES.length)];
    }

}

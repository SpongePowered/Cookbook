package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector2i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.BiomeArea;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.UnmodifiableBiomeArea;

import java.util.Random;

public class BiomeBufferTest {

    public static final Random RANDOM = new Random();
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
        final MutableBiomeArea buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        Assert.assertEquals(Vector2i.ZERO, buffer.getBiomeMin());
        Assert.assertEquals(new Vector2i(19, 14), buffer.getBiomeMax());
        Assert.assertEquals(new Vector2i(20, 15), buffer.getBiomeSize());
        testBuffer(buffer);
        // Test unmodifiable view
        final UnmodifiableBiomeArea unmodifiable = buffer.getUnmodifiableBiomeView();
        Assert.assertEquals(Vector2i.ZERO, unmodifiable.getBiomeMin());
        Assert.assertEquals(new Vector2i(19, 14), unmodifiable.getBiomeMax());
        Assert.assertEquals(new Vector2i(20, 15), unmodifiable.getBiomeSize());
        testBuffer(unmodifiable);
        // Test immutable copy
        final ImmutableBiomeArea copy = buffer.getImmutableBiomeCopy();
        Assert.assertEquals(Vector2i.ZERO, copy.getBiomeMin());
        Assert.assertEquals(new Vector2i(19, 14), copy.getBiomeMax());
        Assert.assertEquals(new Vector2i(20, 15), copy.getBiomeSize());
        testBuffer(copy);
        // Test downsize views
        final MutableBiomeArea downsize = buffer.getBiomeView(new Vector2i(4, 3), new Vector2i(15, 11));
        Assert.assertEquals(new Vector2i(4, 3), downsize.getBiomeMin());
        Assert.assertEquals(new Vector2i(15, 11), downsize.getBiomeMax());
        Assert.assertEquals(new Vector2i(12, 9), downsize.getBiomeSize());
        testBuffer(downsize);
        final UnmodifiableBiomeArea unmodifiableDownsize = unmodifiable.getBiomeView(new Vector2i(4, 3), new Vector2i(15, 11));
        Assert.assertEquals(new Vector2i(4, 3), unmodifiableDownsize.getBiomeMin());
        Assert.assertEquals(new Vector2i(15, 11), unmodifiableDownsize.getBiomeMax());
        Assert.assertEquals(new Vector2i(12, 9), unmodifiableDownsize.getBiomeSize());
        testBuffer(unmodifiableDownsize);
        final ImmutableBiomeArea immutableDownsize = copy.getBiomeView(new Vector2i(4, 3), new Vector2i(15, 11));
        Assert.assertEquals(new Vector2i(4, 3), immutableDownsize.getBiomeMin());
        Assert.assertEquals(new Vector2i(15, 11), immutableDownsize.getBiomeMax());
        Assert.assertEquals(new Vector2i(12, 9), immutableDownsize.getBiomeSize());
        testBuffer(immutableDownsize);
        // Test relative view
        final MutableBiomeArea relative = downsize.getRelativeBiomeView();
        Assert.assertEquals(Vector2i.ZERO, relative.getBiomeMin());
        Assert.assertEquals(new Vector2i(11, 8), relative.getBiomeMax());
        Assert.assertEquals(new Vector2i(12, 9), relative.getBiomeSize());
        testBuffer(relative);
    }

    @Test
    public void testBiomeBufferRotate() {
        final MutableBiomeArea buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(2, 2);
        buffer.setBiome(0, 0, TEST_BIOMES[0]);
        buffer.setBiome(1, 0, TEST_BIOMES[1]);
        buffer.setBiome(0, 1, TEST_BIOMES[2]);
        buffer.setBiome(1, 1, TEST_BIOMES[3]);
        // 90 degrees
        final DiscreteTransform2 _90degrees = DiscreteTransform2.rotationAroundCenter(1, buffer.getBiomeSize());
        MutableBiomeArea rotated = buffer.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(0, 0));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(1, 0));
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(0, 1));
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(1, 1));
        // 180 degrees
        rotated = rotated.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(0, 0));
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(1, 0));
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(0, 1));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(1, 1));
        // 270 degrees
        rotated = rotated.getBiomeView(_90degrees);
        Assert.assertEquals(TEST_BIOMES[1], rotated.getBiome(0, 0));
        Assert.assertEquals(TEST_BIOMES[3], rotated.getBiome(1, 0));
        Assert.assertEquals(TEST_BIOMES[0], rotated.getBiome(0, 1));
        Assert.assertEquals(TEST_BIOMES[2], rotated.getBiome(1, 1));
    }

    private void testBuffer(BiomeArea buffer) {
        final Vector2i min = buffer.getBiomeMin();
        final Vector2i max = buffer.getBiomeMax();
        // Test bound validation
        try {
            buffer.getBiome(min.sub(1, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.getBiome(max.add(1, 0));
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Extra tests for mutable buffers
        if (buffer instanceof MutableBiomeArea) {
            // Also fills the buffer with random data
            testMutableBuffer((MutableBiomeArea) buffer);
        }
        // Test data copy
        final MutableBiomeArea copy = buffer.getBiomeCopy();
        Assert.assertEquals(min, copy.getBiomeMin());
        Assert.assertEquals(max, copy.getBiomeMax());
        Assert.assertEquals(buffer.getBiomeSize(), copy.getBiomeSize());
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                final BiomeType bufferBiome = buffer.getBiome(x, z);
                final BiomeType copyBiome = copy.getBiome(x, z);
                Assert.assertNotNull(copyBiome);
                Assert.assertNotNull(bufferBiome);
                Assert.assertEquals(bufferBiome, copyBiome);
            }
        }
        // Test distinctiveness of copies
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                copy.setBiome(x, z, BiomeTypes.SKY);
            }
        }
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                Assert.assertNotEquals(BiomeTypes.SKY, buffer.getBiome(x, z));
            }
        }
    }

    private void testMutableBuffer(MutableBiomeArea buffer) {
        final Vector2i min = buffer.getBiomeMin();
        final Vector2i max = buffer.getBiomeMax();
        // Test bound validation
        try {
            buffer.setBiome(min.sub(1, 0), BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.setBiome(max.add(1, 0), BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Test fill
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                final BiomeType randomBiome = getRandomBiome();
                buffer.setBiome(x, z, randomBiome);
                Assert.assertEquals(randomBiome, buffer.getBiome(x, z));
            }
        }
    }

    private static BiomeType getRandomBiome() {
        return TEST_BIOMES[RANDOM.nextInt(TEST_BIOMES.length)];
    }

}

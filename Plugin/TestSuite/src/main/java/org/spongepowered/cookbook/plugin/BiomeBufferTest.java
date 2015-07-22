package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector2i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.MutableBiomeArea;

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
        BiomeTypes.SWAMPLAND,
    };

    @Test
    public void testBiomeBuffer() {
        // Test creation
        final MutableBiomeArea buffer = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        // Test bounds
        Assert.assertEquals(Vector2i.ZERO, buffer.getBiomeMin());
        Assert.assertEquals(new Vector2i(19, 14), buffer.getBiomeMax());
        Assert.assertEquals(new Vector2i(20, 15), buffer.getBiomeSize());
        // Test bound validation
        try {
            buffer.setBiome(21, 9, BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        try {
            buffer.setBiome(9, 16, BiomeTypes.SKY);
            Assert.fail();
        } catch (PositionOutOfBoundsException ignored) {
        }
        // Test fill
        for (int x = 0; x < 20; x++) {
            for (int z = 0; z < 15; z++) {
                final BiomeType randomBiome = getRandomBiome();
                buffer.setBiome(x, z, randomBiome);
                Assert.assertEquals(randomBiome, buffer.getBiome(x, z));
            }
        }
        // Test data copy
        final MutableBiomeArea copy = buffer.getBiomeCopy();
        Assert.assertEquals(Vector2i.ZERO, copy.getBiomeMin());
        Assert.assertEquals(new Vector2i(19, 14), copy.getBiomeMax());
        Assert.assertEquals(new Vector2i(20, 15), copy.getBiomeSize());
        for (int x = 0; x < 20; x++) {
            for (int z = 0; z < 15; z++) {
                final BiomeType bufferBiome = buffer.getBiome(x, z);
                final BiomeType copyBiome = copy.getBiome(x, z);
                Assert.assertNotNull(copyBiome);
                Assert.assertNotNull(bufferBiome);
                Assert.assertEquals(bufferBiome, copyBiome);
            }
        }
        // Test distinctiveness of copies
        for (int x = 0; x < 20; x++) {
            for (int z = 0; z < 15; z++) {
                copy.setBiome(x, z, BiomeTypes.SKY);
            }
        }
        for (int x = 0; x < 20; x++) {
            for (int z = 0; z < 15; z++) {
                Assert.assertNotEquals(BiomeTypes.SKY, buffer.getBiome(x, z));
            }
        }
    }

    private static BiomeType getRandomBiome() {
        return TEST_BIOMES[RANDOM.nextInt(TEST_BIOMES.length)];
    }

}

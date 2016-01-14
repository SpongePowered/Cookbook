package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector2i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.DiscreteTransform2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 */
public class BiomeWorkerTest {

    private static final Random RANDOM = new Random();

    @Test
    public void testFill() {
        final MutableBiomeArea area = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final Vector2i min = area.getBiomeMin();
        final Vector2i max = area.getBiomeMax();
        // Fill the reference with random biomes using regular iteration
        final MutableBiomeArea reference = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                reference.setBiome(x, y, BiomeBufferTest.getRandomBiome());

            }
        }
        // Use the fill function to copy the reference
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker = area.getBiomeWorker();
        worker.fill(reference::getBiome);
        // Check if area and reference are the same
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                Assert.assertNotEquals(BiomeTypes.SKY, area.getBiome(x, y));
                Assert.assertEquals(reference.getBiome(x, y), area.getBiome(x, y));

            }
        }
    }

    @Test
    public void testMap() {
        final MutableBiomeArea area = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final Vector2i min = area.getBiomeMin();
        final Vector2i max = area.getBiomeMax();
        // Fill the reference with either sky or a random biome
        final MutableBiomeArea reference = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker = reference.getBiomeWorker();
        worker.fill((x, y) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        // Map sky to a random biome and anything else to sky into a new area
        worker.map(((v, x, y) -> v.getBiome(x, y).equals(BiomeTypes.SKY) ? BiomeBufferTest.getRandomBiome() : BiomeTypes.SKY), area);
        // Check if area and reference follow the mapping rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                if (reference.getBiome(x, y).equals(BiomeTypes.SKY)) {
                    Assert.assertNotEquals(BiomeTypes.SKY, area.getBiome(x, y));
                } else {
                    Assert.assertEquals(BiomeTypes.SKY, area.getBiome(x, y));
                }
            }
        }
    }

    @Test
    public void testMerge() {
        final MutableBiomeArea area = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final Vector2i min = area.getBiomeMin();
        final Vector2i max = area.getBiomeMax();
        // Fill two references with either sky or a random biome (also test with different sized areas)
        final MutableBiomeArea reference1 = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker1 = reference1.getBiomeWorker();
        worker1.fill((x, y) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        final MutableBiomeArea reference2 = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(22, 18);
        final MutableBiomeArea shiftedReference2 = reference2.getBiomeView(DiscreteTransform2.fromTranslation(-42, 71));
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker2 = reference1.getBiomeWorker();
        worker2.fill((x, y) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        // Merge by using the non-sky if one of the two biomes isn't sky or using the first for any other case
        worker1.merge(shiftedReference2, (firstArea, xFirst, yFirst, secondArea, xSecond, ySecond) -> {
                final BiomeType firstBiome = firstArea.getBiome(xFirst, yFirst);
                final BiomeType secondBiome = secondArea.getBiome(xSecond, ySecond);
                if (firstBiome.equals(BiomeTypes.SKY) && !secondBiome.equals(BiomeTypes.SKY)) {
                    return secondBiome;
                }
                return firstBiome;
            },
            area);
        // Check if area and references follow the merging rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                final BiomeType biome = area.getBiome(x, y);
                if (biome.equals(BiomeTypes.SKY)) {
                    Assert.assertEquals(BiomeTypes.SKY, reference1.getBiome(x, y));
                    Assert.assertEquals(BiomeTypes.SKY, reference2.getBiome(x, y));
                } else if (reference1.getBiome(x, y).equals(BiomeTypes.SKY)) {
                    Assert.assertEquals(reference2.getBiome(x, y), biome);
                } else {
                    Assert.assertEquals(reference1.getBiome(x, y), biome);
                }
            }
        }
    }

    @Test
    public void testReduce() {
        final MutableBiomeArea area = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final Vector2i min = area.getBiomeMin();
        final Vector2i max = area.getBiomeMax();
        // Fill the area with either sky or a random biome and hash the coordinate of sky biomes
        int skyHash = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                final BiomeType biome;
                if (RANDOM.nextBoolean()) {
                    biome = BiomeTypes.SKY;
                    skyHash += x | y;
                } else {
                    biome = BiomeBufferTest.getRandomBiome();
                }
                area.setBiome(x, y, biome);
            }
        }
        // Reduce by hashing the coordinates of sky biomes
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker = area.getBiomeWorker();
        final int reduction = worker.reduce((v, x, y, r) -> r + (v.getBiome(x, y).equals(BiomeTypes.SKY) ? x | y : 0), (a, b) -> a + b, 0);
        Assert.assertEquals(skyHash, reduction);
    }

    @Test
    public void testIterate() {
        final MutableBiomeArea area = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 15);
        final Vector2i min = area.getBiomeMin();
        final Vector2i max = area.getBiomeMax();
        // Fill the area with either sky or a random biome and add the coordinate of sky biomes
        final Set<Vector2i> skyCoordinates = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                final BiomeType biome;
                if (RANDOM.nextBoolean()) {
                    biome = BiomeTypes.SKY;
                    skyCoordinates.add(new Vector2i(x, y));
                } else {
                    biome = BiomeBufferTest.getRandomBiome();
                }
                area.setBiome(x, y, biome);
            }
        }
        // Iterate and add the coordinates of sky biomes
        final MutableBiomeAreaWorker<? extends MutableBiomeArea> worker = area.getBiomeWorker();
        final Set<Vector2i> coordinates = new HashSet<>();
        worker.iterate((v, x, y) -> {
            if (v.getBiome(x, y).equals(BiomeTypes.SKY)) {
                coordinates.add(new Vector2i(x, y));
            }
        });
        Assert.assertEquals(skyCoordinates, coordinates);
    }

}

package org.spongepowered.cookbook.plugin;

import com.flowpowered.math.vector.Vector3i;
import org.junit.Assert;
import org.junit.Test;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;

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
        final MutableBiomeVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final Vector3i min = volume.getBiomeMin();
        final Vector3i max = volume.getBiomeMax();
        // Fill the reference with random biomes using regular iteration
        final MutableBiomeVolume reference = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                reference.setBiome(x, 0, z, BiomeBufferTest.getRandomBiome());

            }
        }
        // Use the fill function to copy the reference
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker = volume.getBiomeWorker();
        worker.fill(reference::getBiome);
        // Check if volume and reference are the same
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                Assert.assertNotEquals(BiomeTypes.SKY, volume.getBiome(x, 0, z));
                Assert.assertEquals(reference.getBiome(x, 0, z), volume.getBiome(x, 0, z));

            }
        }
    }

    @Test
    public void testMap() {
        final MutableBiomeVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final Vector3i min = volume.getBiomeMin();
        final Vector3i max = volume.getBiomeMax();
        // Fill the reference with either sky or a random biome
        final MutableBiomeVolume reference = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker = reference.getBiomeWorker();
        worker.fill((x, y, z) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        // Map sky to a random biome and anything else to sky into a new volume
        worker.map(((v, x, y, z) -> v.getBiome(x, y, z).equals(BiomeTypes.SKY) ? BiomeBufferTest.getRandomBiome() : BiomeTypes.SKY), volume);
        // Check if volume and reference follow the mapping rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                if (reference.getBiome(x, 0, z).equals(BiomeTypes.SKY)) {
                    Assert.assertNotEquals(BiomeTypes.SKY, volume.getBiome(x, 0, z));
                } else {
                    Assert.assertEquals(BiomeTypes.SKY, volume.getBiome(x, 0, z));
                }
            }
        }
    }

    @Test
    public void testMerge() {
        final MutableBiomeVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final Vector3i min = volume.getBiomeMin();
        final Vector3i max = volume.getBiomeMax();
        // Fill two references with either sky or a random biome (also test with different sized volumes)
        final MutableBiomeVolume reference1 = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker1 = reference1.getBiomeWorker();
        worker1.fill((x, y, z) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        final MutableBiomeVolume reference2 = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(22, 1, 18);
        final MutableBiomeVolume shiftedReference2 = reference2.getBiomeView(DiscreteTransform3.fromTranslation(-42, 0, 71));
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker2 = reference1.getBiomeWorker();
        worker2.fill((x, y, z) -> RANDOM.nextBoolean() ? BiomeTypes.SKY : BiomeBufferTest.getRandomBiome());
        // Merge by using the non-sky if one of the two biomes isn't sky or using the first for any other case
        worker1.merge(shiftedReference2, (firstVolume, xFirst, yFirst, zFirst, secondVolume, xSecond, ySecond, zSecond) -> {
                    final BiomeType firstBiome = firstVolume.getBiome(xFirst, yFirst, zFirst);
                    final BiomeType secondBiome = secondVolume.getBiome(xSecond, ySecond, zSecond);
                    if (firstBiome.equals(BiomeTypes.SKY) && !secondBiome.equals(BiomeTypes.SKY)) {
                        return secondBiome;
                    }
                    return firstBiome;
                },
                volume);
        // Check if volume and references follow the merging rule
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                final BiomeType biome = volume.getBiome(x, 0, z);
                if (biome.equals(BiomeTypes.SKY)) {
                    Assert.assertEquals(BiomeTypes.SKY, reference1.getBiome(x, 0, z));
                    Assert.assertEquals(BiomeTypes.SKY, reference2.getBiome(x, 0, z));
                } else if (reference1.getBiome(x, 0, z).equals(BiomeTypes.SKY)) {
                    Assert.assertEquals(reference2.getBiome(x, 0, z), biome);
                } else {
                    Assert.assertEquals(reference1.getBiome(x, 0, z), biome);
                }
            }
        }
    }

    @Test
    public void testReduce() {
        final MutableBiomeVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final Vector3i min = volume.getBiomeMin();
        final Vector3i max = volume.getBiomeMax();
        // Fill the volume with either sky or a random biome and hash the coordinate of sky biomes
        int skyHash = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                final BiomeType biome;
                if (RANDOM.nextBoolean()) {
                    biome = BiomeTypes.SKY;
                    skyHash += x | z;
                } else {
                    biome = BiomeBufferTest.getRandomBiome();
                }
                volume.setBiome(x, 0, z, biome);
            }
        }
        // Reduce by hashing the coordinates of sky biomes
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker = volume.getBiomeWorker();
        final int reduction = worker.reduce((v, x, y, z, r) -> r + (v.getBiome(x, y, z).equals(BiomeTypes.SKY) ? x | z : 0), (a, b) -> a + b, 0);
        Assert.assertEquals(skyHash, reduction);
    }

    @Test
    public void testIterate() {
        final MutableBiomeVolume volume = TestSuite.EXTENT_BUFFER_FACTORY.createBiomeBuffer(20, 1, 15);
        final Vector3i min = volume.getBiomeMin();
        final Vector3i max = volume.getBiomeMax();
        // Fill the volume with either sky or a random biome and add the coordinate of sky biomes
        final Set<Vector3i> skyCoordinates = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                final BiomeType biome;
                if (RANDOM.nextBoolean()) {
                    biome = BiomeTypes.SKY;
                    skyCoordinates.add(new Vector3i(x, 0, z));
                } else {
                    biome = BiomeBufferTest.getRandomBiome();
                }
                volume.setBiome(x, 0, z, biome);
            }
        }
        // Iterate and add the coordinates of sky biomes
        final MutableBiomeVolumeWorker<? extends MutableBiomeVolume> worker = volume.getBiomeWorker();
        final Set<Vector3i> coordinates = new HashSet<>();
        worker.iterate((v, x, y, z) -> {
            if (v.getBiome(x, y, z).equals(BiomeTypes.SKY)) {
                coordinates.add(new Vector3i(x, y, z));
            }
        });
        Assert.assertEquals(skyCoordinates, coordinates);
    }

}

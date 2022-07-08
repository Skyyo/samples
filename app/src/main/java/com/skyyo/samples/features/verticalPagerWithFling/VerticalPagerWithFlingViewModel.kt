package com.skyyo.samples.features.verticalPagerWithFling

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerticalPagerWithFlingViewModel @Inject constructor() : ViewModel() {

    val videos = MutableLiveData<List<VideoItem>>()

    init {
        populateListWithFakeData()
    }

    @Suppress("LongMethod")
    private fun populateListWithFakeData() {
        val testVideos = listOf(
            VideoItem(
                1,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
            ),
            VideoItem(
                2,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            ),
            VideoItem(
                3,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
            ),
            VideoItem(
                4,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
            ),
            VideoItem(
                5,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
            ),
            VideoItem(
                6,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg"
            ),
            VideoItem(
                7,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg"
            ),
            VideoItem(
                8,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/Sintel.jpg"
            ),
            VideoItem(
                9,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDrift.jpg"
            ),
            VideoItem(
                10,
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/TearsOfSteel.jpg"
            ),
        )
        val testVideosHlsApple = listOf(
            VideoItem(
                1,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ElephantsDream.jpg"
            ),
            VideoItem(
                2,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/BigBuckBunny.jpg"
            ),
            VideoItem(
                3,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg"
            ),
            VideoItem(
                4,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_fmp4/master.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg"
            ),
            VideoItem(
                5,
                "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/gear0/prog_index.m3u8",
                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg"
            )
        )
        val testVideosHls = listOf(
            VideoItem(
                1,
                "https://d397js2k7uynti.cloudfront.net/522/hls-522.m3u8?Expires=1657723120&Signature=He5Ly8kqKErsKIgAFJitS1oKdvK2qe7nRoAHqq63NjW21hnwuAysZFbj8gAA7UWC90oAqDLhVVw~OadWn95TCo6nOF8O7klrwYw~EZThp1vyzjmM4t~1TugsQ4C-dTMy7VrwQI7Y6GV11cgVfkNvBgqTQEREcmDgndSjeK9u7j4ZK6OoodzZAJPJheh4Sip1OmcPWhfwkfub9SYrbdJzjFE1vHic6TXJ~Mptwozf~hj4RAahl3MdT6SD8h~PDtK6o7KgykCvafH~b4kM5O~uFBEJq2lkrWtDZbpDgqv4iLSy0IhvSBpzm2QFKKvEAbb5JBKVglhVKQ8FD499JhIpig&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/522/image/image.png?Expires=1657723120&Signature=b3uD4sBZt2P71oy04upu3avDUXy6bv6LM~yHztKtWraEE-XiQP9lq2d2SJoziDoT~9-s~z4F4YCma7gjA1ubfRD5iMb4Bwek3kPqNb~jUtr75VmQIzSJ9djcmRgZ2OuMFG8bgDDZeJ6iaB3n5uyr4bmEU5V~JeF3nqWxKzN7rvSPTt7E28yo0Y-1DUZE~s~nu6seu06~Fg8kDx863HE6NMsdqhko7oD2V5Del48vNpHm~ByGDtfVb-TCfQC~UErVHk-kHDeGuDqjGxXrZMGlVq6Zx0X8Y6SG6rtwlRUK2Gp7s7uZRD1BY8seWyZ0OgE9y4Yh7-q2chxVduTMayPUNA&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                2,
                "https://d397js2k7uynti.cloudfront.net/519/hls-519.m3u8?Expires=1657723120&Signature=eetNRYzMbnj0ZYHGmre9oyat-oECdB9P0cAmglHRyJTAOWr-Xng558kqt~kFNaXMP6K322zydxzt2suZS40NOML5iRvGo8ZoFjncN1lfOMG-pI88yuoUAqzi1h3kZNlVIOIJlo5zn3d2pZahLJrjDNU92DGjZzDLCqT5S3TazJeEZQbJHsIiogEEbp94BtK0xI2Kl-Qd5bLNlyhSC3fqU5fvyoFkXmmcIkbNja5No6y9LEgWCdH6s6kG-c4b3bWFdsyLdNiMtbXTB80zUQOXURQf5wMYTZFb9pQNnBN3FNgS5k4iH9pKTTrBZiI4JopcTCdVLg20M7dn2mf0exB7IA&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/519/image/image.png?Expires=1657723120&Signature=a7yfZyKvIUJFwrhshBXaGLFKRk2j5KE33nXAh914B7lIz25h92AXwUk0pSfglvQrEjKSQ-OPdLs9scau45GI~OHTxgk-ZBPghJM7yDAEHXCyGdUCe3uhGkQRNJr1FIBpg~hFTN7aZlwuEq1sMyJZKthd919KIXDUGGy86J~ZMvaXrRZcGD6d8teKoBw3HoV~gqK6CsXXm3v4QanC2eMJ1WdV7BC26KQfU3DR9lbBT9QOFWHW3qQvUoHqfR2c7Yxdvwwd7G1o9VYwGhjOtMh-0aYJH~1aixUyW6XrNaK5FAcz1BOQRGdRn-YU3BIS6QD9XYVRnFsmlM--ydJMcaG9fA&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                3,
                "https://d397js2k7uynti.cloudfront.net/518/hls-518.m3u8?Expires=1657723120&Signature=a4G0roZ55-ufIyrIJYM1WHkOwxOlSzUJ4Gb0mszI8kJmINdFBrapMG7hoktQeazrd9lf0~Vkpw--jlYkEou8NqVelpP65ZQbGn9YNXN6gDc5EfHNxTShEaJzFNyA32c1mXMp01UX~EFTXdnW7-7uMC8FD-uCn52tw5b2Ud07MChJ6TaiU8qgCIqwAZsmGXPVUeRIzCulJJROyRNUNZOJiiuZ5a67lwoOp7o3uI9gPQZPHInCirSQaRcbw7VuaGwBCsvstiO0Hfdo7XbdGGifx64I~ViM~IRjcu-cVwsq65CjMmbdjGLBFklJBAlL55oTWnNPxhU8rEoPbVyT8MC16A&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/518/image/image.png?Expires=1657723120&Signature=I-XDpiMr9QWBQKS6~OW9XbA1yBdW0GQfmF5hGEf6TOgx9hyntdJg8OtstnrAsobwsSXZRgLwKNoMBJEsYOMoXVRtNG1q5f3osncOlHkOiiI2suMORlQT~x1V60TlIG4~5UJec7qvHl4VWH7V1~IL~rD3IYUR5U-jZNTIFT9VKrU4FrN9H95tTEY2XxgTVjck1Hn8eGwFHoM3K5D~0VFf5LHQo9xXuMbtnXVJmh~RpZF2bbMnRqzdMvxxyteSw4GNFMEpUr5YCuT~t~v5Vn9F-A5L8lFZz-spjkY~a7qCsJlCfIxFJGi1jsa6rPaiKlI~UrFDmybobhAsvgEmWgW7DQ&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                4,
                "https://d397js2k7uynti.cloudfront.net/517/hls-517.m3u8?Expires=1657723120&Signature=sQE9QypM96N5aeWk6vbErGOs7yIk3Fnw0hZ4xT81O3qu5hRP3Ckl7vESv06gRK6~yjMm7Vsc4n6tpb5heTN2krF7t4rMp2-OH01-X6C6~ku9rO4oF-ejFL3nA5kjQRYd6zTny3DHytT8LJfDAJa9~HtKXkiIL3443dTSj1ddjn0vbD0-Irtn1LsOVhAXbKlXEehTzNLYE7v2HPn-zuJxR4jzgcFHEqP3Y6j01AzZ54lS79sj0rYxPwzWcMD6717yW1bjUML0o4HTF6JqenMmQ1VZ4sdQQRppG2M1cnUSgUpmFfFf~ZwuK2IEek5kOARlCs-3LjT6~m8gJSQK~ILRcQ__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/517/image/image.png?Expires=1657723120&Signature=FOdaY3qgABg5H3O9Ml2nkT-R9kp7huykjcYjhgxevgcOEY351PNGIfIOBC95z4sa6~dx2DOQnj8HLQNx0999eD7ekoroHPbjn3Ij1MG29hUYKgxj0HFBoy70bIpF~2jF1n7HCJ3-YC1g0cAtv-gH1UTNMhoDrqbz4j~uU5s09R5HjwDkk1-~~oWvMu993CavQmwmUPysNnvRReYBQe~EeILOqbKrnx1wKgqNqeYO0Rja28j1WjZy0zqXAutCvj75t32wWerHGvqEngYpg~vohKiF1Ze3~2CpJJnDS6XOvZhJrPP8x9VN60wYlanJwnT5jJloIqMJZgLASFkTlxef4w__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                5,
                "https://d397js2k7uynti.cloudfront.net/516/hls-516.m3u8?Expires=1657723120&Signature=U~l8Xv-Pyzs6YBJKtpmSeFtGk8-E~1MVoR500IrAmQ1rO3olOcx~LulGdmYZPK4z64uXrEtF541f5K3Qh-3eAVRGjSHg6a5hEyy8NGM19aOEIax3~G3vjF6VonJ94G-ac0KTybVzp33S7ty2UqCsA9uuGBHqYhKPdeALv8B48XY6lMlViSefaQII22gaaYQUWOb8zt1Su49AQGl51TyH1lnSIKtP6uVgukOFRhMfGRD4r7CFy-0R7PqM6fBYK9-bYxtOwGamEtVJ9PEYpdXXiD0lGwuNPWs5lOo7OlXMXRAhVRf-Nw2etGouy8opZNY0gk6ACDqIUFwk-1~Mfzr2mQ__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/516/image/image.png?Expires=1657723120&Signature=MsBDyVjIadj7CDSwrEQc~9RtqUUaHnc8k8IVxRmIbpRW8gFVrvwNxHQP0bGFxoTqjvOpwC2SzqqsiNwAxEUw0I8G4ajNToSVLi-7oEP6srnln9AqJj7biDgmlg91j6fe8vs9qXyQNc9TC51P3GRla6cUhFlfV6F0TvYXdf68F48CjR8klsMZBsN3c4WGs3clDG7oqTLAzwLGaNRtCShLQD~epusQxVLFi2K7yvO6L8ifUUA6NkQTEDDCs0XrKsKrgDCtnf3M9w0nuvjYqhpda-BUX4oFYlxdz80GuMvRUqZcyOHtgmXnH-V3MzcFh0TOQ-ZJ5sI~~JjAtFHgzVubeQ__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                6,
                "https://d397js2k7uynti.cloudfront.net/515/hls-515.m3u8?Expires=1657723120&Signature=WKJyNzUohnfHYvStCr80REgyGRp3NMdzwBCpcEwKaL09u5dN8sdJp3YT35rWjPHxAaC3-m9zAkq0u4XL4qI66IL~wZDE-9xSpa3I5f-J-p~mGXTc2HPUgIkJ9xu6HNT6qHnyxHwzHIh~LdWxhLPhRo9ImdgsHvAuAo1Q70SLHNkaHaCDHS3fenzeAVk4yAQnITP63UmC67wJqLXrK2Mn3MsjdiJ6vMwhDc7l6Ep9FAzvaZ7YkQY~k0reaQ-Ueru6C74SEwH~zCo4iBcR2lUjLbEw2Vk5F-gedO1ETwiUuiMC13ELgxw-XnK-YxKmPVF5jocSKhm~fvCouXioTSQfWA__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/515/image/image.png?Expires=1657723120&Signature=VohRiGi7bmzyIKlo4zLdF-wdD5qnH6mR8IeX~VQAd1Esh4MeJYKqa3lsYyjEtoFzwyV5btWleVix5JZPPCsPCAtagvBsVWH0FpFFxI7NWQL~X5~JklaibK4G1vgs3U-lX6oobqR01hLZKaKayF~l8wU-8y901s1F7BfgXgKOcN8Ko35zesGrB2kQlz4v7kv5TjXwtOERZSmwl-nQ5qkpQw-5~SsyGHM2IKWTOCZGlDOOvo1JizyABA8b0EI56NYdZggwyB3Bx6SZARZOzsUreO7oUR1RmnBtMHCgmdSSEFikmyq9ZlX~uCLEMvQihIyBjZKVYRS~OYVP9g0bBRKAuw__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                7,
                "https://d397js2k7uynti.cloudfront.net/514/hls-514.m3u8?Expires=1657723120&Signature=QPYzNEl949sm3wrZszsGFSYokJbVIMdXhJkeG~TELdeHL0Gt0wN8u37dk66LgeaJOOmgHTY-xaKai4IGCMSlrspwVs9eD9c-JVeIDG73dPqH6RHRcnWMyrfknOdbvCQRosz~ALMN3pezA799TRHsZ0p-DwxN04B1mdhe7bJxyVAes6qr8P-IH9jT~vlaGKsiasdQeTkZhlqHMDkoqgAWJsh6RLhw-WU5m5UrmNKEkL8J1C4OX7rgnqbannmkjlcRgm49XxgVIT6jMZ8qhNA44ZfWfv-pZb8ArL~sKtAy-ALlse3Sfe8-b5lApNblLT6oyCqrW6tw3xw~f14u2XqJJw__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/514/image/image.png?Expires=1657723120&Signature=rSeUf3inzdM3UrYrr4PJyL88B9~tmFeJJESKeko39iZ7nICp~6EzXp5Q2Vsi17whPxeaYh6H52~gJ~6Ffyomge8rcDEgLaE4mS2lFZiziupaTDoKIEfjeSe7lfWZp3zcJVNXo~gRv-zkAY-wwfYjxLsKW-9kYivHFN2FtrVZ46Aq6lVDXhje6zw1ORfqGG9739EvytrlAIFHbsnePvadfM0PNgXu5yjPC3Y52lBj3cMmP-2WjkZ1FhFgdVDeKxMKH6kIof9eb~li2l2vVu~yvUJLsHdHL5CQ-Uc0ZXigSBCljI3bWJhnmWE4zfPQXMFD-YcNwTfgkhk7I97TlZ2aIA__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                8,
                "https://d397js2k7uynti.cloudfront.net/513/hls-513.m3u8?Expires=1657723120&Signature=dUTKd2DGgRwBYtOF26kSKsbwXzO26MXJSiQRkeXNEMS17OxWgqHS6mxgoBHqDMfggammsN3qC2V9fJy2YHF1uuWZRpCh~PUm69SjTyMvW3jQiCZ0QSr-gu5gRqFsrPLFoxw~WB9siunXaBmSoa49tB9th2pzuh8sQ3neEX0MTnhaoN8pjk2olT1nVuneLraLDMdXvbH5GlKIjhbPztN8PM8AQXqXbp4qYHglMrMtG6LDxdYHe6dLVBX2MR7Z73xvw9FncrM8OV~swtCHYqy9~opSPbqHYW60wv1H~gYutmydltkTjBTzEq3SzDLlSQbgWhMoDrQ0mU~v7fheSTjFjQ__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/513/image/image.png?Expires=1657723120&Signature=qoRvaKg5pTMrYq~rR7iT1TmJKzhTQtohyysT6DDevfowyznowaXjXihFj2aq0g~bK2pz0sxuSTUlaER71nLWdGiXlcyWVDWbYMqUuQiOLSJl4Itl~jR41SbbH1qG~7OFiQwqUl3Dt9-Sp2NdeEwX58tVVUPcaoFKCLSIwpPVL16aJhKL5abcq5wIEL9~MI-wFZpC~xvkq8W6KzDT9MJ~07xiMXb~Ci2WfxyVA04BCLPKCgQTA7vIAGbf5494Ml9LAGtFMo7TdFxggxF5vU6Dz8EmxTki-8vzUC1NP5rQbEQRRvFXKgJzNUX~TdbFHcMfAycqfRiKwhoxdpTF4l1bJg__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                9,
                "https://d397js2k7uynti.cloudfront.net/512/hls-512.m3u8?Expires=1657723120&Signature=qiVOOVJTEH1pCvuUq2n1QtR57zGcENT6Kan3ok~sAE7A8UbarpKEu8VPXx0cAuoYwMwdmq0vMcOyjFlOtjcZ6dAuU2YGwgDVHTGiL088BoCrEKve2GC~GqABLffNOItuulQL3AB-2nZDwpUdzBfIRuMo2DJdIZSRbRHwdJCLfTzucm7h7~oT2VBCM~GQa5a6IrwN2yz4G43F9xg3fQU2Gu38csIZXy0IifvcJg~5RyCsZWwkKIx45AR-76D2Bv~bDhjrjW4HSFwAc2YBpeeEjkLRvcAQISHvwPROkV81jTCxV955ukkLVidaQdtdXr-ioqlml159qhA6H3rIaEHajA__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/512/image/image.png?Expires=1657723120&Signature=XoU2n8heWjUaXskMRj3Jlg2U~Y0ClkgNNG98hV0gWOyTldvCRPXtP26vO4WPuY1xQvY1G5eY3ywbv6KG1~nWZ9RCGPcAM7DARTNHFG4fGkOGgi5RSP4aEHv850b0Ra07TgZ6cIeTSYphbIZoKAQb5ZG-Tlascd9LKYx39DxyP5YC1fl~v-AyVPlA-8~D12AvR1RB9R3opwMKURpA365hbNIc1MQiLIfNIfBR~ylSk-M5IagY1QX2PhFQC2xqIPib6W~Anl8ekwRKsxmHtfpeP3pHjy-K~bVgje5isVrCDsXmYP1tk6v46PqY3juGvEkYPv-FW0eENuT3OoWziad8Iw__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
            VideoItem(
                10,
                "https://d397js2k7uynti.cloudfront.net/511/hls-511.m3u8?Expires=1657723120&Signature=WXkA824giaiLtBx8OZC7ZEGMYdzbAJ1gqt6jBGr8T5Hq9JOkVvUY6HJoCvMr7agOlem4ANjlF5OzIHbFQCGcleYhW8UNlT4O1KQWuzhIv0hXeMZw9Af3RssjiZwciIPDSDlZu3LIL~6w9jpWxETYgGN~sYojEQaOGywO6hPWzfmD70ZEVwYl9Pupz-5H99pHnqgYLI4bkb2zwLiOsmYEHbWIwxW1ZTleSvkuK0Euxb1DqtYU4bbCQPre5UDcbjId1oAdwVPCoC9khIxB7DHbJ-7yYkU9xyHpxYM5t8rL56eO0YRVwuxfQQ3tdbBVNoYqCw7kawKqNofdSDnef4OU3w__&Key-Pair-Id=K2CUA5SNY7TH6O",
                "https://d397js2k7uynti.cloudfront.net/511/image/image.png?Expires=1657723120&Signature=EdxHuM3GJ9WrWTmsoUQERhl2P8jvaNcl2e~XZOgVcuAErG9ZtUXum9vSBTuuZjCmboX~AN1jiYdwb5K68FtHfSy5ZtKFEvDAlR-iMQJwFvUFYE~eVYGH70Kub6Xb0KHg5of5yZ2H-sLLiXe4li2raxpAiuUTardGqSE~2nQi7RdfC9R8XNT22iXIuRWUSNMAQkAbecyVQU1R-Eg1mL0RXZcr0mHcmstDj7RWs5YflYVN8OPZ3JTyNgV4ulDeB~Ffm~5UUC7O8qH0IhrgLQVc9dODKxTe-oZalcqpREOXVwxRzLbl9pWHmnqKhpTwC6YgmjxC3DRh0c8mrmj7OQfgEA__&Key-Pair-Id=K2CUA5SNY7TH6O"
            ),
        )
        videos.postValue(testVideosHls)
    }
}

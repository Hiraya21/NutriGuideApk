package com.example.domain.models

data class GuideArticle(
    val id: String,
    val title: String,
    val subtitle: String,
    val summary: String,
    val sections: List<GuideSection>
)

data class GuideSection(
    val sectionTitle: String,
    val content: String,
    val tips: List<String> = emptyList()
)

fun GuideArticle.getLocalized(language: AppLanguage): GuideArticle {
    if (language == AppLanguage.ENGLISH) return this

    return when (this.id) {
        "rice_production" -> when (language) {
            AppLanguage.TAGALOG -> GuideArticle(
                id = this.id,
                title = "Gabay sa Pagtatanim ng Palay",
                subtitle = "Paghahanda ng lupa, pagtatanim, at pag-aalaga ng pananim.",
                summary = "Ang praktikal na librong ito ay tumutulong sa mga magsasaka ng palay na gumawa ng malinaw na desisyon sa bukid sa pamamagitan ng mga simpleng hakbang. Sundin ang lokal na gabay sa agrikultura at mga label ng produkto.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Paghahanda ng Lupa",
                        content = "Ang tamang pag-aararo at pagpapatag ng bukid ay nagtitiyak ng pantay na pamamahagi ng tubig at nakakapigil sa pagtubo ng damo.",
                        tips = listOf(
                            "Mag-araro 14-21 araw bago magtanim.",
                            "Mag-suyod nang dalawang beses kada 7 araw.",
                            "Patagin nang maigi ang bukid para mapanatili ang 2-3 cm na tubig."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "2. Pagpili at Paghahanda ng Binhi",
                        content = "Gumamit ng mataas na kalidad na certified seeds na angkop sa klima at uri ng iyong palayan.",
                        tips = listOf(
                            "Ibabad ang binhi sa malinis na tubig sa loob ng 24 oras.",
                            "Paitlugin sa loob ng 24-36 oras hanggang sa lumabas ang puting sprouts.",
                            "Isabog nang pantay sa punlaan."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "3. Paglilipat-tanim at Pagpapasabog",
                        content = "Maglipat-tanim ng 14-18 araw na punla sa distansyang 20x20 cm, o isabog ang nag-germinate na binhi nang pantay sa basang lupa.",
                        tips = listOf(
                            "Panatilihing 2 punla kada burol para sa palayang may patubig.",
                            "Mag-sulam o magtanim muli sa mga kulang na pwesto sa loob ng 7 araw."
                        )
                    )
                )
            )
            AppLanguage.TAGLISH -> GuideArticle(
                id = this.id,
                title = "Rice Production Guide",
                subtitle = "Step-by-step land prep, seed rate, at fertilizer schedule.",
                summary = "Ang practical booklet na ito ay tumutulong sa rice farmers na mag-decide nang maayos sa bukid using simple steps. Sundin ang local agricultural advice at product labels.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Land Preparation",
                        content = "Ang tamang pagpapatag at pag-aararo ay nagse-secure ng pantay na tubig at nagkokontrol ng damo.",
                        tips = listOf(
                            "Mag-araro 14-21 days bago magtanim.",
                            "Mag-suyod ng 2 times every 7 days.",
                            "I-level nang mabuti para ma-maintain ang 2-3 cm na tubig."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "2. Seed Selection & Prep",
                        content = "Gumamit ng high-quality certified seeds na angkop sa lugar at patubig mo.",
                        tips = listOf(
                            "Ibabad ang seeds sa malinis na tubig for 24 hours.",
                            "I-incubate ng 24-36 hours hanggang lumabas ang sprouts.",
                            "I-sow nang pantay sa seedbed."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "3. Transplanting & Direct Seeding",
                        content = "I-transplant ang 14-18 days old seedlings sa 20x20 cm distance, o mag-broadcast ng sprouted seeds.",
                        tips = listOf(
                            "Maintain 2 seedlings per hill sa irrigated fields.",
                            "Mag-replant sa mga nawawalang pwesto within 7 days."
                        )
                    )
                )
            )
            AppLanguage.ILOCANO -> GuideArticle(
                id = this.id,
                title = "Gabay ti Panagmula ti Pagay",
                subtitle = "Panaghanda ti daga, panagmula, ken panag-atiman.",
                summary = "Daytoy a libro ket tumulong kadagiti mannalon tapno maaramid dagiti nasayaat a desisyon babaen ti nalaka a pagtaudan. Sundin ti lokal a pagtaudan ken lebel ti produkto.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Panaghanda ti Daga",
                        content = "Ti nasayaat a panag-arado ken panagpatag ti talon ket mangipasigurado ti pantay a danum ken mangpedped ti raniag.",
                        tips = listOf(
                            "Mag-arado 14-21 aldaw sakbay ti panagmula.",
                            "Mag-suklay ti maminduadua kada 7 aldaw.",
                            "I-level ti talon tapno mataginayon ti 2-3 cm a danum."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "2. Panagpili ken Panaghanda ti Bukel",
                        content = "Mausar ti nangato ti kalidadna a certified seeds a maitutop ti klima ken patubig.",
                        tips = listOf(
                            "Iseyp ti bukel iti nadalus a danum ti 24 a oras.",
                            "Paitlogen ti 24-36 a oras inggana maipasngay ti puraw a sprouts.",
                            "Iseyp nang pantay iti punlaan."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "3. Panaglipat-mula ken Pagsasabog",
                        content = "Ilipat ti 14-18 aldaw a mula iti 20x20 cm a kaadayo, wenno isabog ti sprouted bukel.",
                        tips = listOf(
                            "Mataginayon ti 2 a mula kada burol ti patubig.",
                            "Magsulam ti karkarna a pwesto iti uneg ti 7 aldaw."
                        )
                    )
                )
            )
            AppLanguage.CEBUANO -> GuideArticle(
                id = this.id,
                title = "Giya sa Pagtanom og Humay",
                subtitle = "Pag-andam sa yuta, pagpili sa binhi, ug pag-atiman.",
                summary = "Kining praktikal nga giya nagtabang sa mga mag-uuma sa humay sa paghimo og maayong desisyon sa kaumahan. Sunda ang lokal nga giya sa agrikultura ug label sa mga produkto.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Pag-andam sa Yuta",
                        content = "Ang hustong pagdaro ug pagpatag sa yuta nagseguro sa patas nga tubig ug naglikay sa pagtubo sa sagbot.",
                        tips = listOf(
                            "Magdaro 14-21 ka adlaw sa wala pa magtanom.",
                            "Magsulay sa makaduha matag 7 ka adlaw.",
                            "I-level og maayo ang yuta aron mamentinar ang 2-3 cm nga tubig."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "2. Pagpili ug Pag-andam sa Binhi",
                        content = "Paggamit og maayong kalidad nga certified seeds nga angay sa imong klima ug yuta.",
                        tips = listOf(
                            "Ihumol ang binhi sa limpyong tubig sa 24 ka oras.",
                            "I-incubate sulod sa 24-36 ka oras hangtod mogawas ang puting chip.",
                            "Isabwag og patas sa punlaan."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "3. Pagtanum ug Pagsabwag",
                        content = "I-transplant ang 14-18 ka adlaw nga punla sa distansya nga 20x20 cm, o isabwag ang miturok nga binhi sa basang yuta.",
                        tips = listOf(
                            "Maintinar ang 2 ka punla matag bungdo sa patubig.",
                            "Mag-replant sa kulang nga pwesto sulod sa 7 ka adlaw."
                        )
                    )
                )
            )
            else -> this
        }

        "fertilizer_guide" -> when (language) {
            AppLanguage.TAGALOG -> GuideArticle(
                id = this.id,
                title = "Gabay sa Pataba",
                subtitle = "Ligtas at epektibong paglalagay ng sustansya.",
                summary = "Unawain ang tamang oras ng paglalagay ng Nitrogen (N), Phosphorus (P), at Potassium (K) upang mapataas ang ani at maiwasan ang aksaya.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Unang Pagpapataba (Basal)",
                        content = "Maglagay ng Complete (14-14-14) o DAP sa loob ng 0-14 araw pagkatanim (DAT) para sa matibay at malusog na ugat.",
                        tips = listOf("Ihalo sa lupa sa huling pagpapatag ng bukid kung maaari.")
                    ),
                    GuideSection(
                        sectionTitle = "2. Pangalawang Pagpapataba (Top Dressing)",
                        content = "Maglagay ng Urea o Ammonium Sulfate sa panahon ng pag-aanak (21-28 DAT) para sa maraming suhi at matibay na tangkay.",
                        tips = listOf("Siguraduhing basain ang lupa bago magsabog ng pataba.")
                    ),
                    GuideSection(
                        sectionTitle = "3. Pagpapataba sa Paglilihi (Panicle Initiation)",
                        content = "Maglagay ng MOP (0-0-60) at Urea sa paglilihi ng palay (40-45 DAT) para lumaki at mapuno ang mga butil.",
                        tips = listOf("Iwasan ang sobrang nitrogen sa huling bahagi para hindi madaling dumapa ang palay.")
                    )
                )
            )
            AppLanguage.TAGLISH -> GuideArticle(
                id = this.id,
                title = "Fertilizer Guide",
                subtitle = "Safe at effective nutrient application.",
                summary = "Intindihin ang proper timing ng Nitrogen (N), Phosphorus (P), at Potassium (K) para ma-maximize ang yield at iwas aksaya.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Basal Application",
                        content = "Mag-apply ng Complete (14-14-14) o DAP sa 0-14 days after transplanting (DAT) para sa healthy roots.",
                        tips = listOf("I-mix sa lupa sa final land leveling kung pwede.")
                    ),
                    GuideSection(
                        sectionTitle = "2. Top Dressing (Mid-Vegetative)",
                        content = "Mag-apply ng Urea o Ammonium Sulfate sa active tillering (21-28 DAT) para madaming suhi at matibay na stem.",
                        tips = listOf("Siguraduhing moist ang bukid bago mag-broadcast ng fertilizer.")
                    ),
                    GuideSection(
                        sectionTitle = "3. Panicle Initiation Split",
                        content = "Mag-apply ng MOP at Urea sa panicle initiation (40-45 DAT) para maganda ang laman ng butil.",
                        tips = listOf("Iwasan ang sobra sa nitrogen sa late season para iwas dapa.")
                    )
                )
            )
            AppLanguage.ILOCANO -> GuideArticle(
                id = this.id,
                title = "Gabay ti Paitaba",
                subtitle = "Ligtas ken epektibo a panagikabil ti paitaba.",
                summary = "Tarusen ti husto nga oras ti Nitrogen (N), Phosphorus (P), ken Potassium (K) tapno ngumato ti ani ken awan ti masayang.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Unang Paitaba (Basal)",
                        content = "Mangikabil ti Complete (14-14-14) wenno DAP iti 0-14 aldaw manipud panagmula (DAT) tapno matagibat ti ramut.",
                        tips = listOf("Ikiwas iti daga iti pinal a panagpatag no mabalin.")
                    ),
                    GuideSection(
                        sectionTitle = "2. Pangduadua a Paitaba (Top Dressing)",
                        content = "Mangikabil ti Urea wenno Ammonium Sulfate ti panag-anak (21-28 DAT) tapno adu ti suhi ken natibay ti tangkay.",
                        tips = listOf("Siguraduen a nabasa ti talon sakbay ti panagsabog ti paitaba.")
                    ),
                    GuideSection(
                        sectionTitle = "3. Paitaba ti Panaglihi (Panicle Initiation)",
                        content = "Mangikabil ti MOP ken Urea ti panaglihi ti pagay (40-45 DAT) tapno dakkel ken nakalaman ti bukel.",
                        tips = listOf("Lisiang ti labes a nitrogen tapno saan a madagdagus a madapa ti pagay.")
                    )
                )
            )
            AppLanguage.CEBUANO -> GuideArticle(
                id = this.id,
                title = "Giya sa Abuno",
                subtitle = "Luwas ug epektibong pag-abuno alang sa humay.",
                summary = "Sabta ang hustong oras sa Nitrogen (N), Phosphorus (P), ug Potassium (K) aron mopadako sa ani ug makadaginot.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "1. Unang Pag-abuno (Basal)",
                        content = "Magbutang og Complete (14-14-14) o DAP sa 0-14 ka adlaw human pagtanom (DAT) alang sa gahi nga gamot.",
                        tips = listOf("I-sagol sa yuta sa katapusang pagpatag kon mahimo.")
                    ),
                    GuideSection(
                        sectionTitle = "2. Ikaduhang Pag-abuno (Top Dressing)",
                        content = "Magbutang og Urea o Ammonium Sulfate sa pagpamasag (21-28 DAT) alang sa daghang ugat ug punoan.",
                        tips = listOf("Siguroha nga basa ang yuta sa wala pa magsabwag og abuno.")
                    ),
                    GuideSection(
                        sectionTitle = "3. Pag-abuno sa Pagmabdos sa Humay (Panicle Initiation)",
                        content = "Magbutang og MOP (0-0-60) ug Urea sa pagmabdos sa humay (40-45 DAT) aron modako ug mapuno ang humay.",
                        tips = listOf("Likayi ang sobra nga nitrogen sa ulahing bahin aron dili dali matumba ang humay.")
                    )
                )
            )
            else -> this
        }

        "irrigation" -> when (language) {
            AppLanguage.TAGALOG -> GuideArticle(
                id = this.id,
                title = "Pangangalaga sa Tubig at Patubig",
                subtitle = "Wastong pamamahala ng tubig sa bawat yugto ng palay.",
                summary = "Ang mga modernong paraan tulad ng Alternate Wetting and Drying (AWD) ay nakakatipid sa tubig nang hindi nababawasan ang ani.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Alternate Wetting & Drying (AWD)",
                        content = "Maglagay ng simpleng perforated pipe (PaniPipe) sa bukid upang masubaybayan ang antas ng tubig sa ilalim ng lupa.",
                        tips = listOf(
                            "Hayaang bumaba ang tubig nang hanggang 15 cm sa ilalim ng lupa bago muling magpatubig.",
                            "Panatilihing may 5 cm na tubig ang bukid sa panahon ng pagdadalaga at pagbubunga."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "Huling Pagpapatuyo (Terminal Drainage)",
                        content = "Patuyuin nang tuluyan ang bukid 10-14 araw bago ang ani upang maging pantay ang pagkahinog ng butil at makapasok ang harvester.",
                        tips = listOf("Nakaiwas sa pagkakabaon ng makina sa putik habang nag-aani.")
                    )
                )
            )
            AppLanguage.TAGLISH -> GuideArticle(
                id = this.id,
                title = "Water & Irrigation Management",
                subtitle = "Water management para sa bawat growth stage.",
                summary = "Smart water techniques tulad ng Alternate Wetting and Drying (AWD) ay nakakatipid ng tubig without reducing yield.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Alternate Wetting & Drying (AWD)",
                        content = "Mag-install ng PaniPipe sa bukid para ma-monitor ang water level sa ilalim ng lupa.",
                        tips = listOf(
                            "Hayaan bumaba ang water depth to 15 cm below soil level bago mag-reflood.",
                            "Keep flooded (5 cm water) during flowering/heading stage."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "Terminal Drainage",
                        content = "I-drain ang bukid 10-14 days before harvest para pantay ang paghinog at handa sa harvester.",
                        tips = listOf("Iwas baon ng harvester machine sa putik.")
                    )
                )
            )
            AppLanguage.ILOCANO -> GuideArticle(
                id = this.id,
                title = "Panangipagpateg ti Danum ken Patubig",
                subtitle = "Nalaka a panag-atiman ti danum iti tunggal yugto.",
                summary = "Ti moderno a teknika a kas ti Alternate Wetting and Drying (AWD) ket makatiped ti danum a saan a maipababa ti ani.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Alternate Wetting & Drying (AWD)",
                        content = "Magikabil ti simple a PaniPipe iti talon tapno masubaybayan ti ngato ti danum iti uneg ti daga.",
                        tips = listOf(
                            "Palubosang bumaba ti danum iti 15 cm iti uneg ti daga sakbay ti panagpatubig ulit.",
                            "Mataginayon ti 5 cm a danum ti panahon ti panagdalaga ken panagbunga."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "Pinal a Panagpatuyo (Terminal Drainage)",
                        content = "Ipatuyo ti talon ti 10-14 aldaw sakbay ti ani tapno pantay ti panaglinak ken makaaramid ti makina.",
                        tips = listOf("Lisiang ti panagbaon ti makina iti pitak.")
                    )
                )
            )
            AppLanguage.CEBUANO -> GuideArticle(
                id = this.id,
                title = "Giya sa Patubig",
                subtitle = "Pangdumala sa tubig sa matag yugto sa pagtubo.",
                summary = "Ang modernong pamaagi sama sa Alternate Wetting and Drying (AWD) nakadaginot sa tubig nga walay pagkunhod sa ani.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Alternate Wetting & Drying (AWD)",
                        content = "Magbutang og perforated pipe (PaniPipe) sa kaumahan aron mabantayan ang lebel sa tubig sa ilalom sa yuta.",
                        tips = listOf(
                            "Pabayai nga mobaba ang tubig sa 15 cm sa ilalom sa yuta sa wala pa magpatubig usab.",
                            "Maintinar ang 5 cm nga tubig sa panahon sa pagpamulak ug pagbunga."
                        )
                    ),
                    GuideSection(
                        sectionTitle = "Terminal Drainage",
                        content = "Pahuwasan og maayo ang kaumahan 10-14 ka adlaw sa wala pa ang ani aron magdungan ang pagkahinog ug makasulod ang harvester.",
                        tips = listOf("Makahasol ug makalikay sa pagkahunlak sa makina sa lapok.")
                    )
                )
            )
            else -> this
        }

        "harvesting" -> when (language) {
            AppLanguage.TAGALOG -> GuideArticle(
                id = this.id,
                title = "Gabay sa Pag-aani",
                subtitle = "Wastong oras ng pag-aani, pagpapatuyo, at pag-iimbak.",
                summary = "Mag-ani kapag 80-85% ng mga butil ay kulay ginto o dilaw na upang maiwasan ang pagkatapon at pagkabitak ng palay.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Tamang Oras ng Pag-aani",
                        content = "Mag-ani sa antas na 20-24% moisture content ng butil.",
                        tips = listOf("Giukin agad pagkatapos gapasin upang maiwasan ang amag.")
                    ),
                    GuideSection(
                        sectionTitle = "Pagpapatuyo at Pag-iimbak",
                        content = "Patuyuin ang palay hanggang 14% moisture para sa maikling pag-iimbak o 12% para sa matagalang binhi.",
                        tips = listOf("Gumamit ng solar mechanical dryer o malinis na lona.")
                    )
                )
            )
            AppLanguage.TAGLISH -> GuideArticle(
                id = this.id,
                title = "Harvesting Guide",
                subtitle = "Tamang timing ng pag-aani, drying, at storage.",
                summary = "Mag-ani kapag 80-85% ng grains ay kulay dilaw/ginto na para iwas shatter loss at cracking.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Optimal Harvest Timing",
                        content = "Mag-ani kapag nasa 20-24% moisture content ang grains.",
                        tips = listOf("I-thresh agad pagkatapos ng paggapas para iwas amag.")
                    ),
                    GuideSection(
                        sectionTitle = "Drying & Moisture Control",
                        content = "I-dry ang palay to 14% moisture for short storage o 12% for long-term seeds.",
                        tips = listOf("Gumamit ng solar dryer o malinis na tarpaulin.")
                    )
                )
            )
            AppLanguage.ILOCANO -> GuideArticle(
                id = this.id,
                title = "Gabay ti Panag-ani",
                subtitle = "Husto nga oras ti panag-ani, panagpatuyo, ken panagdulin.",
                summary = "Mag-ani no 80-85% ti bukel ket dilaw wenno ginto para lisiang ti panagkalat ken panagbitak.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Husto nga Oras ti Panag-ani",
                        content = "Mag-ani no ti moisture content ti bukel ket 20-24%.",
                        tips = listOf("I-giuk ausar ti panag-gapas tapno lisiang ti amag.")
                    ),
                    GuideSection(
                        sectionTitle = "Panagpatuyo ken Panagdulin",
                        content = "Ipatuyo ti pagay inggana 14% moisture para ti maipatalged a dulin wenno 12% para iti bukel.",
                        tips = listOf("Mausar ti solar dryer wenno nadalus a lona.")
                    )
                )
            )
            AppLanguage.CEBUANO -> GuideArticle(
                id = this.id,
                title = "Giya sa Pag-ani",
                subtitle = "Hustong oras sa pag-ani, pagpauga, ug pagtipig.",
                summary = "Mag-ani kon 80-85% sa mga lugas Bulawan o dalag na aron malikayan ang pagkahagbong ug pagkaliki sa humay.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Hustong Oras sa Pag-ani",
                        content = "Mag-ani sa lebel nga 20-24% moisture content sa lugas.",
                        tips = listOf("I-galing o i-giok dayon human pagputol aron malikayan ang amag.")
                    ),
                    GuideSection(
                        sectionTitle = "Pagpauga ug Pagtipig",
                        content = "I-pauga ang humay hangtod 14% moisture para sa mubo nga pagtipig o 12% alang sa mga binhi.",
                        tips = listOf("Paggamit og solar dryer o limpyo nga lona.")
                    )
                )
            )
            else -> this
        }

        "government_programs" -> when (language) {
            AppLanguage.TAGALOG -> GuideArticle(
                id = this.id,
                title = "Programang Pampamahalaan",
                subtitle = "Tulong at serbisyo para sa mga magsasaka ng palay.",
                summary = "Impormasyon sa pautang sa agrikultura, libreng binhi, seguro sa pananim (PCIC), at tulong na makinarya.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Programang RCEF (Rice Competitiveness Enhancement Fund)",
                        content = "Nagbibigay ng mataas na aning binhi, kagamitang makinarya sa bukid, at pagsasanay sa pagsasaka.",
                        tips = listOf("Magpalista sa RSBSA sa opisina ng agrikultura sa inyong bayan.")
                    ),
                    GuideSection(
                        sectionTitle = "Seguro sa Pananim (PCIC)",
                        content = "Nagbibigay ng proteksyong pinansyal sa pinsala ng bagyo, baha, tagtuyot, at peste.",
                        tips = listOf("Magpasa ng aplikasyon ng seguro bago magsimula ang panahon ng pagtatanim.")
                    )
                )
            )
            AppLanguage.TAGLISH -> GuideArticle(
                id = this.id,
                title = "Government Programs",
                subtitle = "Support at libreng ayuda para sa rice farmers.",
                summary = "Information tungkol sa loans, seed subsidies, PCIC crop insurance, at farm machinery.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "RCEF Program",
                        content = "Nagbibigay ng high-yielding seeds, farm equipment, at libreng training.",
                        tips = listOf("Magpa-register sa RSBSA sa Municipal Agriculture Office.")
                    ),
                    GuideSection(
                        sectionTitle = "PCIC Crop Insurance",
                        content = "Nagbibigay ng financial protection laban sa bagyo, baha, drayt, at peste.",
                        tips = listOf("Mag-submit ng insurance application bago magsimula planting season.")
                    )
                )
            )
            AppLanguage.ILOCANO -> GuideArticle(
                id = this.id,
                title = "Programa ti Gobyerno",
                subtitle = "Tulong ken serbisyo para kadagiti mannalon.",
                summary = "Impormasyon ti utang, libre a bukel, seguro ti mula (PCIC), ken tulong a makina.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Programa a RCEF",
                        content = "Mangted ti nangato ti anina a bukel, kagamitan a makina, ken panagsanay.",
                        tips = listOf("Magpalista iti RSBSA iti opisina ti agrikultura iti balaymo.")
                    ),
                    GuideSection(
                        sectionTitle = "Seguro ti Mula (PCIC)",
                        content = "Mangted ti tulong a pinansyal para ti dadael ti bagyo, layus, kikit, ken peste.",
                        tips = listOf("Magpasa ti aplikasyon ti seguro sakbay ti panagmula.")
                    )
                )
            )
            AppLanguage.CEBUANO -> GuideArticle(
                id = this.id,
                title = "Mga Programa sa Gobyerno",
                subtitle = "Tabang ug serbisyo alang sa mga mag-uuma sa humay.",
                summary = "Impormasyon sa pautang sa agrikultura, libreng binhi, seguro sa pananum (PCIC), ug mga makinarya.",
                sections = listOf(
                    GuideSection(
                        sectionTitle = "Programang RCEF (Rice Competitiveness Enhancement Fund)",
                        content = "Naghatag og taas nga ani nga binhi, mga makinarya sa kaumahan, ug libreng pagbansay.",
                        tips = listOf("Magparehistro sa RSBSA sa opisina sa agrikultura sa inyong lungsod.")
                    ),
                    GuideSection(
                        sectionTitle = "Seguro sa Pananum (PCIC)",
                        content = "Naghatag og proteksyon sa pinansyal batok sa bagyo, baha, hulaw, ug peste.",
                        tips = listOf("Magsummite og aplikasyon sa seguro sa wala pa magsugod ang panahon sa pagtanom.")
                    )
                )
            )
            else -> this
        }

        else -> this
    }
}

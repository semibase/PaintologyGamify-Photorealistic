package com.paintology.lite.photorealistic.drawing.Activity

import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Author
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Images
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Links
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Metadata
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.NewDrawing
import com.paintology.lite.photorealistic.drawing.Activity.gallery_activity.model.new_models.Statistic
import com.paintology.lite.photorealistic.drawing.Activity.leader_board.model.CountriesModel
import com.paintology.lite.photorealistic.drawing.Activity.your_ranking.YourRankingModel
import com.paintology.lite.photorealistic.drawing.Model.ChallengeModel
import com.paintology.lite.photorealistic.drawing.Model.NewNotificationType
import com.paintology.lite.photorealistic.drawing.Model.NewNotificationsModel
import com.paintology.lite.photorealistic.drawing.R
import com.paintology.lite.photorealistic.drawing.challenge.ChallengeLevelsModel

object Lists {


    val emptyNewDrawing = NewDrawing(
        id = "",
        title = "",
        description = "",
        createdAt = "",
        type = "",
        tags = emptyList(),
        images = Images(""),
        links = Links(""),
        metadata = Metadata("", "", ""),
        statistic = Statistic(0, 0, 0, 0, 0, 0),
        author = Author("", "", "", "", ""),
        referenceId = ""
    )

    val notificationsCategories = arrayListOf(
        NewNotificationsModel(
            "Chat Request",
            "Hi there! Just wanted to say, I love the cat you've created!",
            "4 : 30 PM",
            NewNotificationType.CHAT,
            "",
            "",
            "Richard Joe",
        ),
        NewNotificationsModel(
            "Friend Request",
            "Hello! I’d like to add you to my Friend List.",
            "04:30 PM",
            NewNotificationType.FRIEND_REQ,
            "",
            "",
            "Richard Joe"
        ),
        NewNotificationsModel(
            "Rating On Your Post",
            "5 star from emma louis",
            "04:30 PM",
            NewNotificationType.RATING_ON_POST,
            "",
            "",
            "Richard Joe",
            3

        ),
        NewNotificationsModel(
            "Comment On Your Post",
            "Nice Drawing!",
            "04:30 PM",
            NewNotificationType.COMMENT,
            "",
            "",
            "Richard Joe"
        ),
        NewNotificationsModel(
            "Challenge Request",
            "Begin Your Next Adventure",
            "04:30 PM",
            NewNotificationType.CHALLENGE_REQ,
            "",
            ""
        ),
        NewNotificationsModel(
            "New Tutorials",
            "Enhance Your Skills with Our Tutorials",
            "04:30 PM",
            NewNotificationType.NEW_TUTORIAL,
            "",
            ""
        ),
        NewNotificationsModel(
            "New Challenge",
            "Enhance Your Skills with Our Challenge",
            "04:30 PM",
            NewNotificationType.CHALLENGE_REQ,
            "",
            ""
        ),
        NewNotificationsModel(
            "Award Gained",
            "Congratulations! You've completed 10 posts in the gallery.",
            "04:30 PM",
            NewNotificationType.AWARD,
            "",
            ""
        ),
    )

//    val tutorials = arrayListOf(
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//        SearchResultModel(
//            "Paintology Gamify",
//            "In this drawing I demonstate the hardness and density settings of the brush tools  in",
//            "#8545",
//            3.5f,
//            9
//        ),
//    )

    val challengeList = arrayListOf(
        ChallengeModel(
            "Paint By Number",
            "Earn 320 Points and get a special bonus from Paintology",
            R.drawable.img_challenge,
            0,
            320,
            R.drawable.bg_red_gradient
        ),
        ChallengeModel(
            "Paint By Number",
            "Earn 320 Points and get a special bonus from Paintology",
            R.drawable.img_paint,
            2,
            320,
            R.drawable.bg_green_gradient
        ),
    )
    val otherTaskList = arrayListOf(
        ChallengeModel(
            "Paint By Number",
            "Earn 320 Points and get a special bonus from Paintology",
            R.drawable.img_first,
            0,
            320,
            R.drawable.bg_gradient_maroon
        ),
        ChallengeModel(
            "Paint By Number",
            "Earn 320 Points and get a special bonus from Paintology",
            R.drawable.img_second,
            0,
            320,
            R.drawable.bg_green_gradient
        ),
        ChallengeModel(
            "Paint By Number",
            "Earn 320 Points and get a special bonus from Paintology",
            R.drawable.img_third,
            0,
            320,
            R.drawable.bg_red_gradient
        ),
    )

    val countriesList = arrayListOf(
        CountriesModel(
            R.drawable.img_world,
            "World",
            "This ranking is compiled from the world's best users.",
            "WW"
        ),
        CountriesModel(
            R.drawable.af___afghanistan,
            "Afghanistan",
            "This ranking is compiled from the best users in Afghanistan",
            "AF"
        ),
        CountriesModel(
            R.drawable.al___albania,
            "Albania",
            "This ranking is compiled from the best users in Albania",
            "AL"
        ),
        CountriesModel(
            R.drawable.dz___algeria,
            "Algeria",
            "This ranking is compiled from the best users in Algeria",
            "DZ"
        ),
        CountriesModel(
            R.drawable.ad___andorra,
            "Andorra",
            "This ranking is compiled from the best users in Andorra",
            "AD"
        ),
        CountriesModel(
            R.drawable.ao___angola,
            "Angola",
            "This ranking is compiled from the best users in Angola",
            "AO"
        ),
        CountriesModel(
            R.drawable.ar___argentina,
            "Argentina",
            "This ranking is compiled from the best users in Argentina",
            "AR"
        ),
        CountriesModel(
            R.drawable.am___armenia,
            "Armenia",
            "This ranking is compiled from the best users in Armenia",
            "AM"
        ),
        CountriesModel(
            R.drawable.au___australia,
            "Australia",
            "This ranking is compiled from the best users in Australia",
            "AU"
        ),
        CountriesModel(
            R.drawable.at___austria,
            "Austria",
            "This ranking is compiled from the best users in Austria",
            "AT"
        ),
        CountriesModel(
            R.drawable.az___azerbaijan,
            "Azerbaijan",
            "This ranking is compiled from the best users in Azerbaijan",
            "AZ"
        ),
        CountriesModel(
            R.drawable.bs___bahamas,
            "Bahamas",
            "This ranking is compiled from the best users in Bahamas",
            "BS"
        ),
        CountriesModel(
            R.drawable.bh___bahrain,
            "Bahrain",
            "This ranking is compiled from the best users in Bahrain",
            "BH"
        ),
        CountriesModel(
            R.drawable.bd___bangladesh,
            "Bangladesh",
            "This ranking is compiled from the best users in Bangladesh",
            "BD"
        ),
        CountriesModel(
            R.drawable.bb___barbados,
            "Barbados",
            "This ranking is compiled from the best users in Barbados",
            "BB"
        ),
        CountriesModel(
            R.drawable.by___belarus,
            "Belarus",
            "This ranking is compiled from the best users in Belarus",
            "BY"
        ),
        CountriesModel(
            R.drawable.be___belgium,
            "Belgium",
            "This ranking is compiled from the best users in Belgium",
            "BE"
        ),
        CountriesModel(
            R.drawable.bz___belize,
            "Belize",
            "This ranking is compiled from the best users in Belize",
            "BZ"
        ),
        CountriesModel(
            R.drawable.bj___benin,
            "Benin",
            "This ranking is compiled from the best users in Benin",
            "BJ"
        ),
        CountriesModel(
            R.drawable.bt___bhutan,
            "Bhutan",
            "This ranking is compiled from the best users in Bhutan",
            "BT"
        ),
        CountriesModel(
            R.drawable.bo___bolivia,
            "Bolivia",
            "This ranking is compiled from the best users in Bolivia",
            "BO"
        ),
        CountriesModel(
            R.drawable.ba___bosnia_and_herzegovina,
            "Bosnia Herzegovina",
            "This ranking is compiled from the best users in Bosnia Herzegovina",
            "BA"
        ),
        CountriesModel(
            R.drawable.bw___botswana,
            "Botswana",
            "This ranking is compiled from the best users in Botswana",
            "BW"
        ),
        CountriesModel(
            R.drawable.br___brazil,
            "Brazil",
            "This ranking is compiled from the best users in Brazil",
            "BR"
        ),
        CountriesModel(
            R.drawable.bn___brunei_darussalam,
            "Brunei",
            "This ranking is compiled from the best users in Brunei",
            "BN"
        ),
        CountriesModel(
            R.drawable.bg___bulgaria,
            "Bulgaria",
            "This ranking is compiled from the best users in Bulgaria",
            "BG"
        ),
        CountriesModel(
            R.drawable.bf___burkina_faso,
            "Burkina Faso",
            "This ranking is compiled from the best users in Burkina Faso",
            "BF"
        ),
        CountriesModel(
            R.drawable.bi___burundi,
            "Burundi",
            "This ranking is compiled from the best users in Burundi",
            "BI"
        ),
        CountriesModel(
            R.drawable.kh___cambodia,
            "Cambodia",
            "This ranking is compiled from the best users in Cambodia",
            "KH"
        ),
        CountriesModel(
            R.drawable.cm___cameroon,
            "Cameroon",
            "This ranking is compiled from the best users in Cameroon",
            "CM"
        ),
        CountriesModel(
            R.drawable.ca___canada,
            "Canada",
            "This ranking is compiled from the best users in Canada",
            "CA"
        ),
        CountriesModel(
            R.drawable.td___chad,
            "Chad",
            "This ranking is compiled from the best users in Chad",
            "TD"
        ),
        CountriesModel(
            R.drawable.cl___chile,
            "Chile",
            "This ranking is compiled from the best users in Chile",
            "CL"
        ),
        CountriesModel(
            R.drawable.cn___china,
            "China",
            "This ranking is compiled from the best users in China",
            "CN"
        ),
        CountriesModel(
            R.drawable.co___colombia,
            "Colombia",
            "This ranking is compiled from the best users in Colombia",
            "CO"
        ),
        CountriesModel(
            R.drawable.km___comoros,
            "Comoros",
            "This ranking is compiled from the best users in Comoros",
            "KM"
        ),
        CountriesModel(
            R.drawable.cg___republic_of_the_congo,
            "Congo",
            "This ranking is compiled from the best users in Congo",
            "CG"
        ),
        CountriesModel(
            R.drawable.hr___croatia__hrvatska_,
            "Croatia",
            "This ranking is compiled from the best users in Croatia",
            "HR"
        ),
        CountriesModel(
            R.drawable.cu___cuba,
            "Cuba",
            "This ranking is compiled from the best users in Cuba",
            "CU"
        ),
        CountriesModel(
            R.drawable.cy___cyprus,
            "Cyprus",
            "This ranking is compiled from the best users in Cyprus",
            "CY"
        ),
        CountriesModel(
            R.drawable.cz___czech_republic,
            "Czech Republic",
            "This ranking is compiled from the best users in Czech Republic",
            "CZ"
        ),
        CountriesModel(
            R.drawable.dk___denmark,
            "Denmark",
            "This ranking is compiled from the best users in Denmark",
            "DK"
        ),
        CountriesModel(
            R.drawable.dj___djibouti,
            "Djibouti",
            "This ranking is compiled from the best users in Djibouti",
            "DJ"
        ),
        CountriesModel(
            R.drawable.dm___dominica,
            "Dominica",
            "This ranking is compiled from the best users in Dominica",
            "DM"
        ),
        CountriesModel(
            R.drawable.do___dominican_republic,
            "Dominican Republic",
            "This ranking is compiled from the best users in Dominican Republic",
            "DO"
        ),
        CountriesModel(
            R.drawable.ec___ecuador,
            "Ecuador",
            "This ranking is compiled from the best users in Ecuador",
            "EC"
        ),
        CountriesModel(
            R.drawable.eg___egypt,
            "Egypt",
            "This ranking is compiled from the best users in Egypt",
            "EG"
        ),
        CountriesModel(
            R.drawable.er___eritrea,
            "Eritrea",
            "This ranking is compiled from the best users in Eritrea",
            "ER"
        ),
        CountriesModel(
            R.drawable.ee___estonia,
            "Estonia",
            "This ranking is compiled from the best users in Estonia",
            "EE"
        ),
        CountriesModel(
            R.drawable.et___ethiopia,
            "Ethiopia",
            "This ranking is compiled from the best users in Ethiopia",
            "ET"
        ),
        CountriesModel(
            R.drawable.fj___fiji,
            "Fiji",
            "This ranking is compiled from the best users in Fiji",
            "FJ"
        ),
        CountriesModel(
            R.drawable.fi___finland,
            "Finland",
            "This ranking is compiled from the best users in Finland",
            "FI"
        ),
        CountriesModel(
            R.drawable.flag_france,
            "France",
            "This ranking is compiled from the best users in France",
            "FR"
        ),
        CountriesModel(
            R.drawable.ga___gabon,
            "Gabon",
            "This ranking is compiled from the best users in Gabon",
            "GA"
        ),
        CountriesModel(
            R.drawable.gm___gambia,
            "Gambia",
            "This ranking is compiled from the best users in Gambia",
            "GM"
        ),
        CountriesModel(
            R.drawable.ge___georgia,
            "Georgia",
            "This ranking is compiled from the best users in Georgia",
            "GE"
        ),
        CountriesModel(
            R.drawable.de___germany,
            "Germany",
            "This ranking is compiled from the best users in Germany",
            "DE"
        ),
        CountriesModel(
            R.drawable.gh___ghana,
            "Ghana",
            "This ranking is compiled from the best users in Ghana",
            "GH"
        ),
        CountriesModel(
            R.drawable.gr___greece,
            "Greece",
            "This ranking is compiled from the best users in Greece",
            "GR"
        ),
        CountriesModel(
            R.drawable.gd___grenada,
            "Grenada",
            "This ranking is compiled from the best users in Grenada",
            "GD"
        ),
        CountriesModel(
            R.drawable.gt___guatemala,
            "Guatemala",
            "This ranking is compiled from the best users in Guatemala",
            "GT"
        ),
        CountriesModel(
            R.drawable.gn___guinea,
            "Guinea",
            "This ranking is compiled from the best users in Guinea",
            "GN"
        ),
        CountriesModel(
            R.drawable.gy___guyana,
            "Guyana",
            "This ranking is compiled from the best users in Guyana",
            "GY"
        ),
        CountriesModel(
            R.drawable.ht___haiti,
            "Haiti",
            "This ranking is compiled from the best users in Haiti",
            "HT"
        ),
        CountriesModel(
            R.drawable.hn___honduras,
            "Honduras",
            "This ranking is compiled from the best users in Honduras",
            "HN"
        ),
        CountriesModel(
            R.drawable.hu___hungary,
            "Hungary",
            "This ranking is compiled from the best users in Hungary",
            "HU"
        ),
        CountriesModel(
            R.drawable.is___iceland,
            "Iceland",
            "This ranking is compiled from the best users in Iceland",
            "IS"
        ),
        CountriesModel(
            R.drawable.in___india,
            "India",
            "This ranking is compiled from the best users in India",
            "IN"
        ),
        CountriesModel(
            R.drawable.id___indonesia,
            "Indonesia",
            "This ranking is compiled from the best users in Indonesia",
            "ID"
        ),
        CountriesModel(
            R.drawable.ir___iran,
            "Iran",
            "This ranking is compiled from the best users in Iran",
            "IR"
        ),
        CountriesModel(
            R.drawable.iq___iraq,
            "Iraq",
            "This ranking is compiled from the best users in Iraq",
            "IQ"
        ),
        CountriesModel(
            R.drawable.ie___ireland,
            "Ireland",
            "This ranking is compiled from the best users in Ireland {Republic}",
            "IE"
        ),
        CountriesModel(
            R.drawable.il___isreal,
            "Israel",
            "This ranking is compiled from the best users in Israel",
            "IL"
        ),
        CountriesModel(
            R.drawable.it___italy,
            "Italy",
            "This ranking is compiled from the best users in Italy",
            "IT"
        ),
        CountriesModel(
            R.drawable.jm___jamaica,
            "Jamaica",
            "This ranking is compiled from the best users in Jamaica",
            "JM"
        ),
        CountriesModel(
            R.drawable.jp___japan,
            "Japan",
            "This ranking is compiled from the best users in Japan",
            "JP"
        ),
        CountriesModel(
            R.drawable.jo___jordan,
            "Jordan",
            "This ranking is compiled from the best users in Jordan",
            "JO"
        ),
        CountriesModel(
            R.drawable.kz___kazakhstan,
            "Kazakhstan",
            "This ranking is compiled from the best users in Kazakhstan",
            "KZ"
        ),
        CountriesModel(
            R.drawable.ke___kenia,
            "Kenya",
            "This ranking is compiled from the best users in Kenya",
            "KE"
        ),
        CountriesModel(
            R.drawable.ki____kiribati,
            "Kiribati",
            "This ranking is compiled from the best users in Kiribati",
            "KI"
        ),
        CountriesModel(
            R.drawable.kp___korea__north_,
            "Korea North",
            "This ranking is compiled from the best users in Korea North",
            "KP"
        ),
        CountriesModel(
            R.drawable.kr___korea__south_,
            "Korea South",
            "This ranking is compiled from the best users in Korea South",
            "KR" // Country code for South Korea
        ),
        CountriesModel(
            R.drawable.flag_kosovo,
            "Kosovo",
            "This ranking is compiled from the best users in Kosovo",
            "XK" // Country code for Kosovo
        ),
        CountriesModel(
            R.drawable.kw___kuwait,
            "Kuwait",
            "This ranking is compiled from the best users in Kuwait",
            "KW" // Country code for Kuwait
        ),
        CountriesModel(
            R.drawable.kg___kyrgyzstan,
            "Kyrgyzstan",
            "This ranking is compiled from the best users in Kyrgyzstan",
            "KG" // Country code for Kyrgyzstan
        ),
        CountriesModel(
            R.drawable.la___laos,
            "Laos",
            "This ranking is compiled from the best users in Laos",
            "LA" // Country code for Laos
        ),
        CountriesModel(
            R.drawable.lv___latvia,
            "Latvia",
            "This ranking is compiled from the best users in Latvia",
            "LV" // Country code for Latvia
        ),
        CountriesModel(
            R.drawable.lb___lebanon,
            "Lebanon",
            "This ranking is compiled from the best users in Lebanon",
            "LB" // Country code for Lebanon
        ),
        CountriesModel(
            R.drawable.ls___lesotho,
            "Lesotho",
            "This ranking is compiled from the best users in Lesotho",
            "LS" // Country code for Lesotho
        ),
        CountriesModel(
            R.drawable.lr___liberia,
            "Liberia",
            "This ranking is compiled from the best users in Liberia",
            "LR" // Country code for Liberia
        ),
        CountriesModel(
            R.drawable.ly___libya,
            "Libya",
            "This ranking is compiled from the best users in Libya",
            "LY" // Country code for Libya
        ),
        CountriesModel(
            R.drawable.li___liechtenstein,
            "Liechtenstein",
            "This ranking is compiled from the best users in Liechtenstein",
            "LI" // Country code for Liechtenstein
        ),
        CountriesModel(
            R.drawable.lt___lithuania,
            "Lithuania",
            "This ranking is compiled from the best users in Lithuania",
            "LT" // Country code for Lithuania
        ),
        CountriesModel(
            R.drawable.lu___luxembourg,
            "Luxembourg",
            "This ranking is compiled from the best users in Luxembourg",
            "LU" // Country code for Luxembourg
        ),
        CountriesModel(
            R.drawable.mk___north_macedonia,
            "Macedonia",
            "This ranking is compiled from the best users in Macedonia",
            "MK" // Country code for North Macedonia
        ),
        CountriesModel(
            R.drawable.mg___madagascar,
            "Madagascar",
            "This ranking is compiled from the best users in Madagascar",
            "MG" // Country code for Madagascar
        ),
        CountriesModel(
            R.drawable.mw___malawi,
            "Malawi",
            "This ranking is compiled from the best users in Malawi",
            "MW" // Country code for Malawi
        ),
        CountriesModel(
            R.drawable.my___malaysia,
            "Malaysia",
            "This ranking is compiled from the best users in Malaysia",
            "MY" // Country code for Malaysia
        ),
        CountriesModel(
            R.drawable.mv___maldives,
            "Maldives",
            "This ranking is compiled from the best users in Maldives",
            "MV" // Country code for Maldives
        ),
        CountriesModel(
            R.drawable.ml___mali,
            "Mali",
            "This ranking is compiled from the best users in Mali",
            "ML" // Country code for Mali
        ),
        CountriesModel(
            R.drawable.mt___malta,
            "Malta",
            "This ranking is compiled from the best users in Malta",
            "MT" // Country code for Malta
        ),
        CountriesModel(
            R.drawable.mh___marshall_islands,
            "Marshall Islands",
            "This ranking is compiled from the best users in Marshall Islands",
            "MH" // Country code for Marshall Islands
        ),
        CountriesModel(
            R.drawable.mr___mauritania,
            "Mauritania",
            "This ranking is compiled from the best users in Mauritania",
            "MR" // Country code for Mauritania
        ),
        CountriesModel(
            R.drawable.mu___mauritius,
            "Mauritius",
            "This ranking is compiled from the best users in Mauritius",
            "MU" // Country code for Mauritius
        ),
        CountriesModel(
            R.drawable.mx___mexico,
            "Mexico",
            "This ranking is compiled from the best users in Mexico",
            "MX" // Country code for Mexico
        ),
        CountriesModel(
            R.drawable.fm___federated_states_of_micronesia,
            "Micronesia",
            "This ranking is compiled from the best users in Micronesia",
            "FM" // Federated States of Micronesia
        ),
        CountriesModel(
            R.drawable.md___moldova,
            "Moldova",
            "This ranking is compiled from the best users in Moldova",
            "MD" // Moldova
        ),
        CountriesModel(
            R.drawable.mc___monaco,
            "Monaco",
            "This ranking is compiled from the best users in Monaco",
            "MC" // Monaco
        ),
        CountriesModel(
            R.drawable.mn___mongolia,
            "Mongolia",
            "This ranking is compiled from the best users in Mongolia",
            "MN" // Mongolia
        ),
        CountriesModel(
            R.drawable.me___montenegro,
            "Montenegro",
            "This ranking is compiled from the best users in Montenegro",
            "ME" // Montenegro
        ),
        CountriesModel(
            R.drawable.ma___morocco,
            "Morocco",
            "This ranking is compiled from the best users in Morocco",
            "MA" // Morocco
        ),
        CountriesModel(
            R.drawable.mz___mozambique,
            "Mozambique",
            "This ranking is compiled from the best users in Mozambique",
            "MZ" // Mozambique
        ),
        CountriesModel(
            R.drawable.mm___myanmar,
            "Myanmar",
            "This ranking is compiled from the best users in Myanmar",
            "MM" // Myanmar
        ),
        CountriesModel(
            R.drawable.na___namibia,
            "Namibia",
            "This ranking is compiled from the best users in Namibia",
            "NA" // Namibia
        ),
        CountriesModel(
            R.drawable.nr___nauru,
            "Nauru",
            "This ranking is compiled from the best users in Nauru",
            "NR" // Nauru
        ),
        CountriesModel(
            R.drawable.np___nepal,
            "Nepal",
            "This ranking is compiled from the best users in Nepal",
            "NP" // Nepal
        ),
        CountriesModel(
            R.drawable.nl___netherlands,
            "Netherlands",
            "This ranking is compiled from the best users in Netherlands",
            "NL" // Netherlands
        ),
        CountriesModel(
            R.drawable.nz___new_zealand__aotearoa_,
            "New Zealand",
            "This ranking is compiled from the best users in New Zealand",
            "NZ" // New Zealand
        ),
        CountriesModel(
            R.drawable.ni___nicaragua,
            "Nicaragua",
            "This ranking is compiled from the best users in Nicaragua",
            "NI" // Nicaragua
        ),
        CountriesModel(
            R.drawable.ne___niger,
            "Niger",
            "This ranking is compiled from the best users in Niger",
            "NE" // Niger
        ),
        CountriesModel(
            R.drawable.ng___nigeria,
            "Nigeria",
            "This ranking is compiled from the best users in Nigeria",
            "NG" // Nigeria
        ),
        CountriesModel(
            R.drawable.no___norway,
            "Norway",
            "This ranking is compiled from the best users in Norway",
            "NO" // Norway
        ),
        CountriesModel(
            R.drawable.om___oman,
            "Oman",
            "This ranking is compiled from the best users in Oman",
            "OM" // Oman
        ),
        CountriesModel(
            R.drawable.pk___pakistan,
            "Pakistan",
            "This ranking is compiled from the best users in Pakistan",
            "PK" // Pakistan
        ),
        CountriesModel(
            R.drawable.pw___palau,
            "Palau",
            "This ranking is compiled from the best users in Palau",
            "PW" // Palau
        ),
        CountriesModel(
            R.drawable.ps___palestinian_territory,
            "Palestine",
            "This ranking is compiled from the best users in Palestine",
            "PS" // Palestine
        ),
        CountriesModel(
            R.drawable.pa___panama,
            "Panama",
            "This ranking is compiled from the best users in Panama",
            "PA" // Panama
        ),
        CountriesModel(
            R.drawable.pg___papua_new_guinea,
            "Papua New Guinea",
            "This ranking is compiled from the best users in Papua New Guinea",
            "PG" // Papua New Guinea
        ),
        CountriesModel(
            R.drawable.py___paraguay,
            "Paraguay",
            "This ranking is compiled from the best users in Paraguay",
            "PY" // Paraguay
        ),
        CountriesModel(
            R.drawable.pe___peru,
            "Peru",
            "This ranking is compiled from the best users in Peru",
            "PE" // Peru
        ),
        CountriesModel(
            R.drawable.ph___philippines,
            "Philippines",
            "This ranking is compiled from the best users in Philippines",
            "PH" // Philippines
        ),
        CountriesModel(
            R.drawable.pl___poland,
            "Poland",
            "This ranking is compiled from the best users in Poland",
            "PL" // Poland
        ),
        CountriesModel(
            R.drawable.pt___portugal,
            "Portugal",
            "This ranking is compiled from the best users in Portugal",
            "PT" // Portugal
        ),
        CountriesModel(
            R.drawable.qa___qatar,
            "Qatar",
            "This ranking is compiled from the best users in Qatar",
            "QA" // Qatar
        ),
        CountriesModel(
            R.drawable.ro___romania,
            "Romania",
            "This ranking is compiled from the best users in Romania",
            "RO" // Romania
        ),
        CountriesModel(
            R.drawable.ru___russian_federation,
            "Russian Federation",
            "This ranking is compiled from the best users in Russian Federation",
            "RU" // Russian Federation
        ),
        CountriesModel(
            R.drawable.rw___rwanda,
            "Rwanda",
            "This ranking is compiled from the best users in Rwanda",
            "RW" // Rwanda
        ),
        CountriesModel(
            R.drawable.kn___saint_kitts_and_nevis,
            "St Kitts & Nevis",
            "This ranking is compiled from the best users in St Kitts & Nevis",
            "KN" // Saint Kitts & Nevis
        ),
        CountriesModel(
            R.drawable.lc___saint_lucia,
            "St Lucia",
            "This ranking is compiled from the best users in St Lucia",
            "LC" // Saint Lucia
        ),
        CountriesModel(
            R.drawable.vc___saint_vincent_and_the_grenadines,
            "Saint Vincent & the Grenadines",
            "This ranking is compiled from the best users in Saint Vincent & the Grenadines",
            "VC" // Saint Vincent & the Grenadines
        ),
        CountriesModel(
            R.drawable.ws___samoa,
            "Samoa",
            "This ranking is compiled from the best users in Samoa",
            "WS" // Samoa
        ),
        CountriesModel(
            R.drawable.sm___san_marino,
            "San Marino",
            "This ranking is compiled from the best users in San Marino",
            "SM" // San Marino
        ),
        CountriesModel(
            R.drawable.st___sao_tome_and_principe,
            "Sao Tome & Principe",
            "This ranking is compiled from the best users in Sao Tome & Principe",
            "ST" // Sao Tome & Principe
        ),
        CountriesModel(
            R.drawable.sa___saudi_arabia,
            "Saudi Arabia",
            "This ranking is compiled from the best users in Saudi Arabia",
            "SA" // Saudi Arabia
        ),
        CountriesModel(
            R.drawable.sn___senegal,
            "Senegal",
            "This ranking is compiled from the best users in Senegal",
            "SN" // Senegal
        ),
        CountriesModel(
            R.drawable.rs___serbia,
            "Serbia",
            "This ranking is compiled from the best users in Serbia",
            "RS" // Serbia
        ),
        CountriesModel(
            R.drawable.sc___seychelles,
            "Seychelles",
            "This ranking is compiled from the best users in Seychelles",
            "SC" // Seychelles
        ),
        CountriesModel(
            R.drawable.sl___sierra_leone,
            "Sierra Leone",
            "This ranking is compiled from the best users in Sierra Leone",
            "SL" // Sierra Leone
        ),
        CountriesModel(
            R.drawable.sg___singapore,
            "Singapore",
            "This ranking is compiled from the best users in Singapore",
            "SG" // Singapore
        ),
        CountriesModel(
            R.drawable.sk___slovakia,
            "Slovakia",
            "This ranking is compiled from the best users in Slovakia",
            "SK" // Slovakia
        ),
        CountriesModel(
            R.drawable.si___slovenia,
            "Slovenia",
            "This ranking is compiled from the best users in Slovenia",
            "SI" // Slovenia
        ),
        CountriesModel(
            R.drawable.sb___solomon_islands,
            "Solomon Islands",
            "This ranking is compiled from the best users in Solomon Islands",
            "SB" // Solomon Islands
        ),
        CountriesModel(
            R.drawable.so___somalia,
            "Somalia",
            "This ranking is compiled from the best users in Somalia",
            "SO" // Somalia
        ),
        CountriesModel(
            R.drawable.za___south_africa,
            "South Africa",
            "This ranking is compiled from the best users in South Africa",
            "ZA" // South Africa
        ),
        CountriesModel(
            R.drawable.ss___south_sudan,
            "South Sudan",
            "This ranking is compiled from the best users in South Sudan",
            "SS" // South Sudan
        ),
        CountriesModel(
            R.drawable.es___spain,
            "Spain",
            "This ranking is compiled from the best users in Spain",
            "ES" // Spain
        ),
        CountriesModel(
            R.drawable.lk___sri_lanka,
            "Sri Lanka",
            "This ranking is compiled from the best users in Sri Lanka",
            "LK" // Sri Lanka
        ),
        CountriesModel(
            R.drawable.sd___sudan,
            "Sudan",
            "This ranking is compiled from the best users in Sudan",
            "SD" // Sudan
        ),
        CountriesModel(
            R.drawable.sr___suriname,
            "Suriname",
            "This ranking is compiled from the best users in Suriname",
            "SR" // Suriname
        ),
        CountriesModel(
            R.drawable.se___sweden,
            "Sweden",
            "This ranking is compiled from the best users in Sweden",
            "SE" // Sweden
        ),
        CountriesModel(
            R.drawable.ch___switzerland,
            "Switzerland",
            "This ranking is compiled from the best users in Switzerland",
            "CH" // Switzerland
        ),
        CountriesModel(
            R.drawable.sy___syria,
            "Syria",
            "This ranking is compiled from the best users in Syria",
            "SY" // Syria
        ),
        CountriesModel(
            R.drawable.tw___taiwan,
            "Taiwan",
            "This ranking is compiled from the best users in Taiwan",
            "TW" // Taiwan
        ),
        CountriesModel(
            R.drawable.tj___tajikistan,
            "Tajikistan",
            "This ranking is compiled from the best users in Tajikistan",
            "TJ" // Tajikistan
        ),
        CountriesModel(
            R.drawable.tz___tanzania,
            "Tanzania",
            "This ranking is compiled from the best users in Tanzania",
            "TZ" // Tanzania
        ),
        CountriesModel(
            R.drawable.th___thailand,
            "Thailand",
            "This ranking is compiled from the best users in Thailand",
            "TH" // Thailand
        ),
        CountriesModel(
            R.drawable.tg___togo,
            "Togo",
            "This ranking is compiled from the best users in Togo",
            "TG" // Togo
        ),
        CountriesModel(
            R.drawable.to___tonga,
            "Tonga",
            "This ranking is compiled from the best users in Tonga",
            "TO" // Tonga
        ),
        CountriesModel(
            R.drawable.tt___trinidad_and_tobago,
            "Trinidad & Tobago",
            "This ranking is compiled from the best users in Trinidad & Tobago",
            "TT" // Trinidad & Tobago
        ),
        CountriesModel(
            R.drawable.tn___tunisia,
            "Tunisia",
            "This ranking is compiled from the best users in Tunisia",
            "TN" // Tunisia
        ),
        CountriesModel(
            R.drawable.tr___turkey,
            "Turkey",
            "This ranking is compiled from the best users in Turkey",
            "TR" // Turkey
        ),
        CountriesModel(
            R.drawable.tm___turkmenistan,
            "Turkmenistan",
            "This ranking is compiled from the best users in Turkmenistan",
            "TM" // Turkmenistan
        ),
        CountriesModel(
            R.drawable.tv___tuvalu,
            "Tuvalu",
            "This ranking is compiled from the best users in Tuvalu",
            "TV" // Tuvalu
        ),
        CountriesModel(
            R.drawable.img_uae,
            "United Arab Emirates",
            "This ranking is compiled from the best users in UAE",
            "AE" // United Arab Emirates
        ),
        CountriesModel(
            R.drawable.ug___uganda,
            "Uganda",
            "This ranking is compiled from the best users in Uganda",
            "UG" // Uganda
        ),
        CountriesModel(
            R.drawable.ua___ukraine,
            "Ukraine",
            "This ranking is compiled from the best users in Ukraine",
            "UA" // Ukraine
        ),
        CountriesModel(
            R.drawable.gb_ukm___united_kingdom,
            "United Kingdom",
            "This ranking is compiled from the best users in United Kingdom",
            "GB" // United Kingdom
        ),
        CountriesModel(
            R.drawable.us___united_states,
            "United States",
            "This ranking is compiled from the best users in United States",
            "US" // United States
        ),
        CountriesModel(
            R.drawable.uy___uruguay,
            "Uruguay",
            "This ranking is compiled from the best users in Uruguay",
            "UY" // Uruguay
        ),
        CountriesModel(
            R.drawable.uz___uzbekistan,
            "Uzbekistan",
            "This ranking is compiled from the best users in Uzbekistan",
            "UZ" // Uzbekistan
        ),
        CountriesModel(
            R.drawable.vu___vanuatu,
            "Vanuatu",
            "This ranking is compiled from the best users in Vanuatu",
            "VU" // Vanuatu
        ),
        CountriesModel(
            R.drawable.va___vatican_city_state,
            "Vatican City",
            "This ranking is compiled from the best users in Vatican City",
            "VA" // Vatican City
        ),
        CountriesModel(
            R.drawable.ve___venezuela,
            "Venezuela",
            "This ranking is compiled from the best users in Venezuela",
            "VE" // Venezuela
        ),
        CountriesModel(
            R.drawable.vn___vietnam,
            "Vietnam",
            "This ranking is compiled from the best users in Vietnam",
            "VN" // Vietnam
        ),
        CountriesModel(
            R.drawable.ye___yemen,
            "Yemen",
            "This ranking is compiled from the best users in Yemen",
            "YE" // Yemen
        ),
        CountriesModel(
            R.drawable.zm___zambia,
            "Zambia",
            "This ranking is compiled from the best users in Zambia",
            "ZM" // Zambia
        ),
        CountriesModel(
            R.drawable.zw___zimbabwe,
            "Zimbabwe",
            "This ranking is compiled from the best users in Zimbabwe",
            "ZW" // Zimbabwe
        )
    )


    fun getCountryFlagResource(countryCode: String): Int {
        return when (countryCode) {
            "AF" -> R.drawable.af___afghanistan
            "AL" -> R.drawable.al___albania
            "DZ" -> R.drawable.dz___algeria
            "AD" -> R.drawable.ad___andorra
            "AO" -> R.drawable.ao___angola
            "AR" -> R.drawable.ar___argentina
            "AM" -> R.drawable.am___armenia
            "AU" -> R.drawable.au___australia
            "AT" -> R.drawable.at___austria
            "AZ" -> R.drawable.az___azerbaijan
            "BS" -> R.drawable.bs___bahamas
            "BH" -> R.drawable.bh___bahrain
            "BD" -> R.drawable.bd___bangladesh
            "BB" -> R.drawable.bb___barbados
            "BY" -> R.drawable.by___belarus
            "BE" -> R.drawable.be___belgium
            "BZ" -> R.drawable.bz___belize
            "BJ" -> R.drawable.bj___benin
            "BT" -> R.drawable.bt___bhutan
            "BO" -> R.drawable.bo___bolivia
            "BA" -> R.drawable.ba___bosnia_and_herzegovina
            "BW" -> R.drawable.bw___botswana
            "BR" -> R.drawable.br___brazil
            "BN" -> R.drawable.bn___brunei_darussalam
            "BG" -> R.drawable.bg___bulgaria
            "BF" -> R.drawable.bf___burkina_faso
            "BI" -> R.drawable.bi___burundi
            "KH" -> R.drawable.kh___cambodia
            "CM" -> R.drawable.cm___cameroon
            "CA" -> R.drawable.ca___canada
            "TD" -> R.drawable.td___chad
            "CL" -> R.drawable.cl___chile
            "CN" -> R.drawable.cn___china
            "CO" -> R.drawable.co___colombia
            "KM" -> R.drawable.km___comoros
            "CG" -> R.drawable.cg___republic_of_the_congo
            "HR" -> R.drawable.hr___croatia__hrvatska_
            "CU" -> R.drawable.cu___cuba
            "CY" -> R.drawable.cy___cyprus
            "CZ" -> R.drawable.cz___czech_republic
            "DK" -> R.drawable.dk___denmark
            "DJ" -> R.drawable.dj___djibouti
            "DM" -> R.drawable.dm___dominica
            "DO" -> R.drawable.do___dominican_republic
            "EC" -> R.drawable.ec___ecuador
            "EG" -> R.drawable.eg___egypt
            "ER" -> R.drawable.er___eritrea
            "EE" -> R.drawable.ee___estonia
            "ET" -> R.drawable.et___ethiopia
            "FJ" -> R.drawable.fj___fiji
            "FI" -> R.drawable.fi___finland
            "FR" -> R.drawable.flag_france
            "GA" -> R.drawable.ga___gabon
            "GM" -> R.drawable.gm___gambia
            "GE" -> R.drawable.ge___georgia
            "DE" -> R.drawable.de___germany
            "GH" -> R.drawable.gh___ghana
            "GR" -> R.drawable.gr___greece
            "GD" -> R.drawable.gd___grenada
            "GT" -> R.drawable.gt___guatemala
            "GN" -> R.drawable.gn___guinea
            "GY" -> R.drawable.gy___guyana
            "HT" -> R.drawable.ht___haiti
            "HN" -> R.drawable.hn___honduras
            "HU" -> R.drawable.hu___hungary
            "IS" -> R.drawable.is___iceland
            "IN" -> R.drawable.in___india
            "ID" -> R.drawable.id___indonesia
            "IR" -> R.drawable.ir___iran
            "IQ" -> R.drawable.iq___iraq
            "IE" -> R.drawable.ie___ireland
            "IL" -> R.drawable.il___isreal
            "IT" -> R.drawable.it___italy
            "JM" -> R.drawable.jm___jamaica
            "JP" -> R.drawable.jp___japan
            "JO" -> R.drawable.jo___jordan
            "KZ" -> R.drawable.kz___kazakhstan
            "KE" -> R.drawable.ke___kenia
            "KI" -> R.drawable.ki____kiribati
            "KP" -> R.drawable.kp___korea__north_
            "KR" -> R.drawable.kr___korea__south_
            "XK" -> R.drawable.flag_kosovo
            "KW" -> R.drawable.kw___kuwait
            "KG" -> R.drawable.kg___kyrgyzstan
            "LA" -> R.drawable.la___laos
            "LV" -> R.drawable.lv___latvia
            "LB" -> R.drawable.lb___lebanon
            "LS" -> R.drawable.ls___lesotho
            "LR" -> R.drawable.lr___liberia
            "LY" -> R.drawable.ly___libya
            "LI" -> R.drawable.li___liechtenstein
            "LT" -> R.drawable.lt___lithuania
            "LU" -> R.drawable.lu___luxembourg
            "MK" -> R.drawable.mk___north_macedonia
            "MG" -> R.drawable.mg___madagascar
            "MW" -> R.drawable.mw___malawi
            "MY" -> R.drawable.my___malaysia
            "MV" -> R.drawable.mv___maldives
            "ML" -> R.drawable.ml___mali
            "MT" -> R.drawable.mt___malta
            "MH" -> R.drawable.mh___marshall_islands
            "MR" -> R.drawable.mr___mauritania
            "MU" -> R.drawable.mu___mauritius
            "MX" -> R.drawable.mx___mexico
            "FM" -> R.drawable.fm___federated_states_of_micronesia
            "MD" -> R.drawable.md___moldova
            "MC" -> R.drawable.mc___monaco
            "MN" -> R.drawable.mn___mongolia
            "ME" -> R.drawable.me___montenegro
            "MA" -> R.drawable.ma___morocco
            "MZ" -> R.drawable.mz___mozambique
            "MM" -> R.drawable.mm___myanmar
            "NA" -> R.drawable.na___namibia
            "NR" -> R.drawable.nr___nauru
            "NP" -> R.drawable.np___nepal
            "NL" -> R.drawable.nl___netherlands
            "NZ" -> R.drawable.nz___new_zealand__aotearoa_
            "NI" -> R.drawable.ni___nicaragua
            "NE" -> R.drawable.ne___niger
            "NG" -> R.drawable.ng___nigeria
            "NO" -> R.drawable.no___norway
            "OM" -> R.drawable.om___oman
            "PK" -> R.drawable.pk___pakistan
            "PW" -> R.drawable.pw___palau
            "PS" -> R.drawable.ps___palestinian_territory
            "PA" -> R.drawable.pa___panama
            "PG" -> R.drawable.pg___papua_new_guinea
            "PY" -> R.drawable.py___paraguay
            "PE" -> R.drawable.pe___peru
            "PH" -> R.drawable.ph___philippines
            "PL" -> R.drawable.pl___poland
            "PT" -> R.drawable.pt___portugal
            "QA" -> R.drawable.qa___qatar
            "RO" -> R.drawable.ro___romania
            "RS" -> R.drawable.rs___serbia
            "SC" -> R.drawable.sc___seychelles
            "SL" -> R.drawable.sl___sierra_leone
            "SG" -> R.drawable.sg___singapore
            "SK" -> R.drawable.sk___slovakia
            "SI" -> R.drawable.si___slovenia
            "SB" -> R.drawable.sb___solomon_islands
            "SO" -> R.drawable.so___somalia
            "ZA" -> R.drawable.za___south_africa
            "SS" -> R.drawable.ss___south_sudan
            "ES" -> R.drawable.es___spain
            "LK" -> R.drawable.lk___sri_lanka
            "SD" -> R.drawable.sd___sudan
            "SR" -> R.drawable.sr___suriname
            "SE" -> R.drawable.se___sweden
            "CH" -> R.drawable.ch___switzerland
            "SY" -> R.drawable.sy___syria
            "TW" -> R.drawable.tw___taiwan
            "TJ" -> R.drawable.tj___tajikistan
            "TZ" -> R.drawable.tz___tanzania
            "TH" -> R.drawable.th___thailand
            "TG" -> R.drawable.tg___togo
            "TO" -> R.drawable.to___tonga
            "TT" -> R.drawable.tt___trinidad_and_tobago
            "TN" -> R.drawable.tn___tunisia
            "TR" -> R.drawable.tr___turkey
            "TM" -> R.drawable.tm___turkmenistan
            "TV" -> R.drawable.tv___tuvalu
            "AE" -> R.drawable.img_uae
            "UG" -> R.drawable.ug___uganda
            "UA" -> R.drawable.ua___ukraine
            "GB" -> R.drawable.gb_ukm___united_kingdom
            "US" -> R.drawable.us___united_states
            "UY" -> R.drawable.uy___uruguay
            "UZ" -> R.drawable.uz___uzbekistan
            "VU" -> R.drawable.vu___vanuatu
            "VA" -> R.drawable.va___vatican_city_state
            "VE" -> R.drawable.ve___venezuela
            "VN" -> R.drawable.vn___vietnam
            "YE" -> R.drawable.ye___yemen
            "ZM" -> R.drawable.zm___zambia
            "ZW" -> R.drawable.zw___zimbabwe
            else -> R.drawable.img_world
        }
    }


    val challengeLevelList = arrayListOf(
        ChallengeLevelsModel(
            R.drawable.img_bronze,
            "Beginner 1",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_bronze,
            "Beginner 2",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_bronze,
            "Beginner 3",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_silver,
            "Intermediate 1",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_silver,
            "Intermediate 2",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_silver,
            "Intermediate 3",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_advanced,
            "Advanced 1",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_advanced,
            "Advanced 2",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_advanced,
            "Advanced 3",
            "50 users are participating in this challenge"
        ),
        ChallengeLevelsModel(
            R.drawable.img_expert,
            "Expert",
            "50 users are participating in this challenge"
        ),
    )

    val rankingList = arrayListOf(
        YourRankingModel(
            1,
            R.drawable.img_expert,
            "Expert",
            "0"
        ),
        YourRankingModel(
            2,
            R.drawable.img_advance_3,
            "Advanced 3",
            "0"
        ),
        YourRankingModel(
            3,
            R.drawable.img_advance_2,
            "Advanced 2",
            "0"
        ),
        YourRankingModel(
            4,
            R.drawable.img_advance_1,
            "Advanced 1",
            "0"
        ),
        YourRankingModel(
            5,
            R.drawable.img_intermidiate_3,
            "Intermediate 3",
            "0"
        ),
        YourRankingModel(
            6,
            R.drawable.img_intermidiate_2,
            "Intermediate 2",
            "0"
        ),
        YourRankingModel(
            7,
            R.drawable.img_intermidiate_1,
            "Intermediate 1",
            "0"
        ),
        YourRankingModel(
            8,
            R.drawable.img_beginner_3,
            "Beginner 3",
            "0"
        ),
        YourRankingModel(
            9,
            R.drawable.img_beginner_2,
            "Beginner 2",
            "0"
        ),
        YourRankingModel(
            10,
            R.drawable.img_beginner_1,
            "Beginner 1",
            "0"
        )
    )
}

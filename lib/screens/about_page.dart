import 'dart:math';

import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:material_symbols_icons/material_symbols_icons.dart';
import 'package:settings_tiles/settings_tiles.dart';
import '../utils/open_links.dart';
import 'package:flutter_markdown/flutter_markdown.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:hive/hive.dart';
import 'package:expressive_loading_indicator/expressive_loading_indicator.dart';

class AboutPage extends StatefulWidget {
  const AboutPage({super.key});

  @override
  State<AboutPage> createState() => _AboutPageState();
}

class _AboutPageState extends State<AboutPage> {
  final ScrollController _scrollController = ScrollController();

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final colorTheme = Theme.of(context).colorScheme;
    return Scaffold(
      appBar: AppBar(
        automaticallyImplyLeading: false,
        backgroundColor: Theme.of(context).colorScheme.surfaceContainer,
        elevation: 0,
        toolbarHeight: 120,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(28),
                bottomRight: Radius.circular(28))),
        title: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
                spacing: 10,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    "WeatherMaster",
                    style: TextStyle(
                        color: Theme.of(context).colorScheme.onSurface,
                        fontSize: 24),
                  ),
                  Row(
                    spacing: 2,
                    children: [
                      CheckUpdateButton(),
                      FilledButton.tonal(
                        onPressed: () {
                          showModalBottomSheet(
                            context: context,
                            isScrollControlled: true,
                            backgroundColor: Colors.transparent,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.vertical(
                                  top: Radius.circular(28)),
                            ),
                            builder: (context) {
                              return DraggableScrollableSheet(
                                initialChildSize: 0.4,
                                minChildSize: 0.4,
                                maxChildSize: 0.9,
                                snap: true,
                                builder: (context, scrollController) {
                                  return Scaffold(
                                    backgroundColor: Colors.transparent,
                                    appBar: AppBar(
                                      toolbarHeight: 65,
                                      elevation: 0,
                                      automaticallyImplyLeading: false,
                                      shape: RoundedRectangleBorder(
                                          borderRadius: BorderRadius.only(
                                              topLeft: Radius.circular(28),
                                              topRight: Radius.circular(28))),
                                      title: Text("changelog".tr()),
                                      backgroundColor:
                                          colorTheme.surfaceContainerLow,
                                      scrolledUnderElevation: 1,
                                      actions: [
                                        IconButton.filledTonal(
                                            onPressed: () {
                                              Navigator.pop(context);
                                            },
                                            icon: Icon(Icons.close)),
                                        SizedBox(
                                          width: 10,
                                        )
                                      ],
                                    ),
                                    body: Container(
                                      decoration: BoxDecoration(
                                        color: colorTheme.surfaceContainerLow,
                                        boxShadow: [
                                          BoxShadow(
                                            blurRadius: 10,
                                            color: Colors.black26,
                                            offset: Offset(0, -2),
                                          ),
                                        ],
                                      ),
                                      child: ChangelogSheet(
                                          scrollController: scrollController),
                                    ),
                                  );
                                },
                              );
                            },
                          );
                        },
                        style: ButtonStyle(
                          padding: WidgetStateProperty.all(
                            EdgeInsets.symmetric(horizontal: 12, vertical: 5),
                          ),
                          shape: WidgetStateProperty.all(
                            RoundedRectangleBorder(
                              borderRadius: BorderRadius.only(
                                  topLeft: Radius.circular(3),
                                  topRight: Radius.circular(50),
                                  bottomLeft: Radius.circular(3),
                                  bottomRight: Radius.circular(50)),
                            ),
                          ),
                          minimumSize: WidgetStateProperty.all(Size(0, 30)),
                          iconAlignment: IconAlignment.end,
                          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
                        ),
                        child: Text("whats_new".tr(),
                            style: TextStyle(fontWeight: FontWeight.w700)),
                      ),
                    ],
                  )
                ]),
            Container(
                clipBehavior: Clip.hardEdge,
                decoration:
                    BoxDecoration(borderRadius: BorderRadius.circular(50)),
                child: Image.asset(
                  "assets/weather-icons/new_icon.png",
                  width: 76,
                  height: 76,
                ))
          ],
        ),
      ),
      body: RawScrollbar(
        thumbVisibility: true,
        controller: _scrollController,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(50)),
        thumbColor: colorTheme.outlineVariant,
        thickness: 3,
        child: ListView(
          controller: _scrollController,
          primary: false,
          children: [
            SizedBox(
              height: 10,
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(radius: 23, child: Icon(Symbols.license)),
              title: Text("licenses".tr()),
              subtitle: Text("GNU GPL-3.0"),
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.mail),
              ),
              title: Text("email_text".tr()),
              subtitle: Text("pranshul.devmain@gmail.com"),
              onTap: () {
                openLink("mailto:pranshul.devmain@gmail.com");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.code),
              ),
              title: Text("source_code".tr()),
              subtitle: Text("on_github".tr()),
              onTap: () {
                openLink("https://github.com/PranshulGG/WeatherMaster");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.bug_report),
              ),
              title: Text("create_an_issue".tr()),
              subtitle: Text("on_github".tr()),
              onTap: () {
                openLink("https://github.com/PranshulGG/WeatherMaster/issues/");
              },
            ),
            ListTile(
              minTileHeight: 65,
              leading: CircleAvatar(
                radius: 23,
                child: Icon(Symbols.apps),
              ),
              title: Text("more_apps".tr()),
              subtitle: Text("view".tr()),
              onTap: () {
                showModalBottomSheet(
                  context: context,
                  showDragHandle: true,
                  shape: RoundedRectangleBorder(
                    borderRadius:
                        BorderRadius.vertical(top: Radius.circular(28)),
                  ),
                  builder: (ctx) {
                    return StatefulBuilder(builder: (ctx2, setSt) {
                      return Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          SettingSection(styleTile: true, tiles: [
                            SettingActionTile(
                                icon: CircleAvatar(
                                  child: SvgPicture.string(
                                      ''' <svg width="221" height="222" viewBox="0 0 221 222" fill="none" xmlns="http://www.w3.org/2000/svg">
<circle cx="110.5" cy="110.528" r="110.5" fill="#F3F1E8"/>
<path d="M111 111.028L55 188.028" stroke="#2B1F0D" stroke-width="2" stroke-linecap="round"/>
<path d="M188 55.0278L112 110.028" stroke="#32351E" stroke-width="5" stroke-linecap="round"/>
<path d="M63 78.0278L112 114.028" stroke="#4C4400" stroke-width="16" stroke-linecap="round"/>
</svg>
 '''),
                                ),
                                title: Text("ClockMaster"),
                                description: Text("View on Github"),
                                trailing: Icon(Symbols.open_in_new),
                                onTap: () {
                                  openLink(
                                      "https://github.com/PranshulGG/ClockMaster");
                                }),
                            SettingActionTile(
                                icon: CircleAvatar(
                                  backgroundColor: Colors.transparent,
                                  child: SvgPicture.string(
                                      ''' <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="442" height="512" viewBox="0 0 512 512">
                <path
                        d="M0 0 C1.54982283 -0.00679667 1.54982283 -0.00679667 3.13095522 -0.01373065 C6.5626512 -0.02630147 9.99418699 -0.024759 13.42590332 -0.02310181 C15.89913129 -0.02909174 18.37235739 -0.03589836 20.84558105 -0.04345703 C26.84962104 -0.05925214 32.85359904 -0.06556354 38.85765404 -0.06668179 C43.7452013 -0.06763579 48.63273573 -0.07176359 53.52027893 -0.07808304 C67.41478044 -0.0956792 81.30925118 -0.10492481 95.2037639 -0.10342209 C96.32499245 -0.10330222 96.32499245 -0.10330222 97.46887207 -0.10317993 C98.59148968 -0.10305729 98.59148968 -0.10305729 99.73678643 -0.10293217 C111.84804204 -0.1021204 123.95917296 -0.12123482 136.07039255 -0.14945666 C148.54278823 -0.17828779 161.01512332 -0.19199127 173.48755312 -0.19026911 C180.47502699 -0.18960858 187.46237024 -0.19495119 194.44981384 -0.21662521 C201.02942331 -0.23690518 207.60880609 -0.23605959 214.18844604 -0.22159195 C216.58963933 -0.2195157 218.99085026 -0.22413318 221.39201355 -0.23631287 C238.81663057 -0.31890138 255.8456801 0.65237273 271.97473145 7.95492554 C272.75477539 8.29741333 273.53481934 8.63990112 274.33850098 8.99276733 C301.4769379 21.28822026 322.71144288 43.60796674 333.45568848 71.37142944 C337.95682062 83.45014135 340.47195763 95.21931822 340.42965698 108.11192322 C340.4341881 109.14584401 340.43871921 110.17976479 340.44338763 111.24501652 C340.45595849 114.67904645 340.45441599 118.11291629 340.45275879 121.54696655 C340.45874869 124.02181703 340.46555529 126.49666565 340.47311401 128.97151184 C340.48890874 134.97922682 340.49522048 140.98687986 340.49633877 146.99460984 C340.49729271 151.88454937 340.50142016 156.77447608 340.50774002 161.66441154 C340.52533532 175.56397871 340.53458181 189.46351512 340.53307907 203.3630935 C340.53299916 204.11084192 340.53291925 204.85859034 340.53283691 205.6289978 C340.53275515 206.37767185 340.53267339 207.1263459 340.53258915 207.89770704 C340.53177719 220.01610469 340.55089658 232.13437778 340.57911365 244.25273944 C340.60793614 256.73078035 340.62164875 269.20876071 340.6199261 281.68683571 C340.61926532 288.67819005 340.62461921 295.66941387 340.6462822 302.66073799 C340.66654814 309.24280669 340.66572526 315.82464868 340.65124893 322.40674782 C340.64917116 324.8099737 340.65379944 327.21321716 340.66596985 329.61641312 C340.80723471 359.4662189 334.07533622 386.30833916 314.16223145 409.26742554 C313.49836426 410.07566772 312.83449707 410.88390991 312.1505127 411.71664429 C300.09898469 425.90670591 284.04766589 436.33223992 266.59973145 442.64242554 C265.97671143 442.86905884 265.35369141 443.09569214 264.71179199 443.32919312 C249.37099616 448.49328681 234.45332334 448.70501389 218.42858887 448.67147827 C215.95314731 448.67718979 213.47770839 448.68415706 211.00227356 448.692276 C205.00844295 448.70828759 199.01476339 448.71032544 193.0209262 448.7050631 C188.14251887 448.70100059 183.26414235 448.70248946 178.38573647 448.70781136 C177.68802407 448.70855978 176.99031168 448.7093082 176.27145648 448.7100793 C174.85346465 448.71161098 173.43547283 448.71314933 172.01748101 448.71469427 C158.75731604 448.72838246 145.49721027 448.72300781 132.2370467 448.71150906 C120.14476405 448.70158246 108.05263455 448.71451395 95.96037551 448.73844325 C83.50028112 448.76291189 71.04025783 448.77242301 58.58013994 448.76579052 C51.60226729 448.76231523 44.62451223 448.76441748 37.64665794 448.78204155 C31.08093925 448.79842105 24.51546332 448.79346191 17.94973564 448.7747364 C15.55296125 448.77107366 13.15616491 448.7740906 10.75941086 448.78462219 C-19.06647449 448.90558052 -45.89815916 442.16363733 -68.83776855 422.26742554 C-69.64601074 421.60355835 -70.45425293 420.93969116 -71.2869873 420.25570679 C-85.47704893 408.20417878 -95.90258294 392.15285999 -102.21276855 374.70492554 C-102.43940186 374.08190552 -102.66603516 373.4588855 -102.89953613 372.81698608 C-108.06191581 357.48128204 -108.27536863 342.56905004 -108.24182129 326.5496521 C-108.24753285 324.07583306 -108.25450013 321.60201665 -108.26261902 319.12820435 C-108.27863097 313.13804871 -108.28066828 307.14804422 -108.27540611 301.157882 C-108.27134331 296.28186694 -108.27283283 291.4058827 -108.27815437 286.52986908 C-108.2789028 285.83244541 -108.27965122 285.13502174 -108.28042232 284.41646402 C-108.28195401 282.99903791 -108.28349236 281.58161182 -108.28503729 280.16418573 C-108.29872591 266.90875939 -108.29335076 253.65339226 -108.28185207 240.39796731 C-108.27192777 228.3128267 -108.28485293 216.22783937 -108.30878627 204.1427224 C-108.33326225 191.68827324 -108.3427641 179.23389521 -108.33613354 166.77942252 C-108.33265956 159.80543036 -108.33475149 152.83155586 -108.35238457 145.85758209 C-108.36877543 139.29432265 -108.36379365 132.73130588 -108.34507942 126.16803741 C-108.34141936 123.77329561 -108.34442557 121.37853183 -108.35496521 118.98381042 C-108.42564615 101.58200689 -107.44286205 84.56197989 -100.15026855 68.45492554 C-99.63653687 67.28485962 -99.63653687 67.28485962 -99.11242676 66.09115601 C-86.81697383 38.95271908 -64.49722735 17.7182141 -36.73376465 6.97396851 C-24.65769108 2.47381953 -12.88988959 -0.04232232 0 0 Z M-56.14245605 59.66586304 C-74.5505871 81.22558072 -76.15384753 107.39018127 -76.09655762 134.49008179 C-76.09927415 136.67187618 -76.10283877 138.85366966 -76.10717773 141.03546143 C-76.11622529 146.91146508 -76.11278925 152.7873978 -76.10641956 158.66340256 C-76.10131975 164.84269131 -76.106062 171.02197441 -76.10919189 177.20126343 C-76.11282001 187.57391438 -76.10806284 197.94653675 -76.09851074 208.31918335 C-76.08762845 220.27356666 -76.09115484 232.22788172 -76.10216355 244.18226337 C-76.11126943 254.48682644 -76.11250562 264.79136908 -76.10728621 275.09593487 C-76.10418461 281.23254704 -76.10370701 287.36912725 -76.11035919 293.5057373 C-76.11618025 299.27774337 -76.11204955 305.04966233 -76.10057068 310.82165909 C-76.09794936 312.92698142 -76.09864531 315.03231121 -76.10309601 317.13763046 C-76.15748526 345.50292673 -72.79651742 371.57470588 -52.32946777 392.79476929 C-30.68520806 414.1627193 -4.03629879 416.42055413 24.90319824 416.41268921 C27.10543372 416.41568487 29.30766868 416.41908833 31.50990295 416.42286682 C37.46881633 416.43166991 43.42771533 416.43404339 49.38663459 416.43468261 C53.116424 416.43539524 56.8462098 416.43753359 60.57599831 416.44017982 C72.93624103 416.44894514 85.29647613 416.45360105 97.65672185 416.45284935 C98.32135984 416.45280939 98.98599783 416.45276944 99.67077637 416.45272827 C100.66896855 416.45266695 100.66896855 416.45266695 101.68732623 416.45260439 C112.46199903 416.45219829 123.23663683 416.46176175 134.01129951 416.47586664 C145.10844829 416.49027758 156.20558267 416.49695167 167.30274135 416.49627286 C173.51934015 416.49601885 179.73589931 416.49881653 185.95248985 416.50945091 C191.80402116 416.51918826 197.65547536 416.51917269 203.50700951 416.51193428 C205.643388 416.5108954 207.77977143 416.51320948 209.91614151 416.51929474 C237.99133757 416.59402672 263.702353 413.00159578 284.6895752 392.75912476 C306.29784643 370.87143622 308.31553718 343.85763028 308.30749512 314.61239624 C308.31049145 312.43133996 308.31389506 310.2502842 308.31767273 308.06922913 C308.32646882 302.17228928 308.32884899 296.27536398 308.32948852 290.37841821 C308.33020153 286.68630528 308.3323406 282.99419599 308.33498573 279.30208397 C308.34422472 266.39900861 308.34832245 253.49594355 308.34753418 240.59286499 C308.34693382 228.60166529 308.35748116 216.6105209 308.37327784 204.6193307 C308.38639143 194.29364096 308.39171647 183.96796588 308.39107877 173.64226788 C308.3908249 167.48880914 308.39361448 161.33539046 308.40425682 155.18194008 C308.41400225 149.38948803 308.41397317 143.59711383 308.40674019 137.80465889 C308.40570231 135.69127538 308.40800928 133.57788686 308.41410065 131.46451187 C308.49409118 101.76419362 304.42610977 76.28214702 283.16223145 54.26742554 C261.97950348 33.61781992 235.30925109 32.11458271 207.42126465 32.12216187 C205.21902917 32.11916621 203.01679421 32.11576275 200.81455994 32.11198425 C194.85564656 32.10318117 188.89674756 32.10080769 182.9378283 32.10016847 C179.20803889 32.09945584 175.47825309 32.09731748 171.74846458 32.09467125 C159.38822186 32.08590594 147.02798676 32.08125002 134.66774104 32.08200172 C134.00310305 32.08204168 133.33846506 32.08208164 132.65368652 32.0821228 C131.65549434 32.08218412 131.65549434 32.08218412 130.63713666 32.08224668 C119.86246386 32.08265278 109.08782606 32.07308933 98.31316338 32.05898444 C87.2160146 32.04457349 76.11888022 32.03789941 65.02172154 32.03857821 C58.80512274 32.03883223 52.58856358 32.03603454 46.37197304 32.02540016 C40.52044174 32.01566281 34.66898753 32.01567839 28.81745338 32.02291679 C26.68107489 32.02395568 24.54469146 32.0216416 22.40832138 32.01555634 C-8.24064842 31.93397335 -34.22927027 36.86129343 -56.14245605 59.66586304 Z "
                        fill="#ffb5a0" transform="translate(139.8377685546875,31.732574462890625)" />
                <path
                        d="M0 0 C13.32448879 10.02732614 22.9535142 23.71553756 25.71875 40.38671875 C27.54003179 57.30946201 24.09946878 73.08772814 13.48901367 86.56686401 C9.04980269 92.07422887 4.20589391 97.06224384 -0.8125 102.03955078 C-1.76976328 102.99624821 -2.72658302 103.95338962 -3.68299866 104.91093445 C-6.25030136 107.4788049 -8.8236697 110.04051397 -11.39827347 112.60106134 C-14.09913727 115.28932002 -16.7942401 117.98334248 -19.48999023 120.67672729 C-24.58206859 125.76261937 -29.67921789 130.84339693 -34.77826405 135.92230213 C-40.58885804 141.71044772 -46.39369214 147.50435543 -52.19807816 153.29872537 C-64.12570892 165.20551883 -76.06045908 177.10514921 -88 189 C-86.78686158 189.00358253 -86.78686158 189.00358253 -85.54921532 189.00723743 C-65.86904065 189.06637706 -46.18898855 189.14192052 -26.50894737 189.23571491 C-16.99181602 189.28067778 -7.47472213 189.32009133 2.04248047 189.34643555 C10.33781184 189.36941163 18.63302472 189.40280533 26.92826211 189.44870156 C31.32041391 189.47263903 35.71244831 189.49136437 40.10466385 189.49761391 C44.23988839 189.5036624 48.37481251 189.52365363 52.50992584 189.55427551 C54.02641336 189.56305953 55.54294013 189.56677756 57.05945206 189.56500816 C59.13337472 189.56335261 61.20638463 189.58007686 63.28018188 189.60127258 C64.43979746 189.60588155 65.59941303 189.61049051 66.79416847 189.61523914 C71.08194934 190.12985453 74.47220431 191.57381542 77.64453125 194.53515625 C82.4152943 200.77384639 82.714865 205.19775914 82 213 C79.69987035 218.15229042 76.19660914 220.94889987 71 223 C67.48124287 223.55912471 64.01929951 223.54054123 60.46206665 223.50793457 C58.88841089 223.51729451 58.88841089 223.51729451 57.28296411 223.52684355 C53.77386213 223.54277538 50.26545457 223.53016787 46.75634766 223.51757812 C44.23792618 223.52299919 41.71950788 223.53012176 39.20109558 223.53881836 C33.07412958 223.55516425 26.94741992 223.55277159 20.82045056 223.5411678 C15.83879411 223.5321248 10.85719255 223.53091322 5.87553024 223.53526306 C4.81094889 223.53618246 4.81094889 223.53618246 3.72486084 223.53712043 C2.28293414 223.53839937 0.84100746 223.53969623 -0.6009192 223.5410108 C-14.11593581 223.55241162 -27.63084383 223.5393116 -41.14584263 223.51780933 C-52.73172295 223.4999465 -64.3174429 223.50303665 -75.90332031 223.52148438 C-89.36771586 223.54291326 -102.8320162 223.5513049 -116.29642642 223.53903532 C-117.73358383 223.53776117 -119.17074127 223.53650369 -120.60789871 223.53526306 C-121.31494373 223.53464596 -122.02198874 223.53402886 -122.75045937 223.53339306 C-127.72267428 223.52994562 -132.69482862 223.53575555 -137.66703415 223.54518127 C-144.36644809 223.55760236 -151.06551339 223.54869879 -157.76491356 223.52560425 C-160.22189647 223.52032112 -162.67890489 223.5218241 -165.13587761 223.53065491 C-168.49362681 223.54160925 -171.85041137 223.52784654 -175.20809937 223.50793457 C-176.17708267 223.51685746 -177.14606597 223.52578034 -178.1444124 223.53497362 C-186.0347843 223.44441333 -191.69180311 221.16305182 -198.25 216.875 C-204.18256171 210.6727764 -206.54548811 204.52254129 -206.58743286 195.93397522 C-206.23972775 185.55373302 -202.77978244 175.47226228 -199.92089844 165.56616211 C-198.87797656 161.94782694 -197.86642283 158.32182339 -196.859375 154.69335938 C-190.51243163 132.07856194 -183.22264808 115.74610614 -166.35229492 98.93237305 C-165.30433733 97.87705464 -164.25699732 96.82112265 -163.21022034 95.76463318 C-160.39483992 92.92724847 -157.57010736 90.09936254 -154.74275374 87.2739141 C-152.37200837 84.90328933 -150.00563811 82.52831953 -147.63910657 80.15348941 C-142.05246308 74.54769069 -136.45777442 68.95001171 -130.8581543 63.35717773 C-125.10307669 57.60874681 -119.36293948 51.84569564 -113.62974089 46.0754506 C-108.68390513 41.0988327 -103.72836261 36.13202415 -98.76516992 31.17271644 C-95.81094265 28.22054651 -92.86052525 25.26481652 -89.9196682 22.29932213 C-87.15394928 19.5111951 -84.37621825 16.73559909 -81.58919334 13.9687748 C-80.57515025 12.95811608 -79.56525678 11.94327179 -78.5601387 10.92373657 C-70.75372913 3.01322484 -62.69097047 -2.66740077 -52.125 -6.3125 C-51.26132812 -6.61800781 -50.39765625 -6.92351562 -49.5078125 -7.23828125 C-33.08376032 -12.22689523 -14.33940232 -9.1267606 0 0 Z M-59.57104492 38.89233398 C-60.58477392 39.89599077 -61.59953961 40.89860137 -62.61524963 41.9002533 C-65.35005652 44.60393746 -68.06889894 47.32314088 -70.78404403 50.04655337 C-73.06288595 52.32958062 -75.34929595 54.60495469 -77.63561994 56.88048512 C-83.03637711 62.2561681 -88.42398313 67.64478284 -93.8034668 73.04174805 C-99.32720288 78.5829807 -104.87249075 84.1019395 -110.42898864 89.61030334 C-115.22166821 94.36302286 -120.0004621 99.12940181 -124.76865655 103.90668541 C-127.60651768 106.74969479 -130.44908258 109.58751661 -133.30516624 112.41222954 C-135.99456303 115.07328336 -138.66600196 117.75121555 -141.32542992 120.44220924 C-142.75207863 121.87765406 -144.19575606 123.29611408 -145.64016724 124.71368408 C-154.38813649 133.63101948 -159.72724142 144.00451459 -162.87109375 156.1015625 C-163.18917747 157.25620499 -163.18917747 157.25620499 -163.51368713 158.43417358 C-163.94951631 160.0266774 -164.37852688 161.62106373 -164.80053711 163.21728516 C-165.44704881 165.66109441 -166.11530384 168.09819079 -166.78710938 170.53515625 C-167.21218005 172.09862104 -167.63605442 173.66241169 -168.05859375 175.2265625 C-168.25537216 175.94945465 -168.45215057 176.6723468 -168.65489197 177.41714478 C-169.53636001 180.76833767 -170 183.50380352 -170 187 C-159.95330543 184.77507864 -149.99029002 182.26940912 -140.0625 179.5625 C-139.0216626 179.28656006 -137.9808252 179.01062012 -136.90844727 178.72631836 C-114.58998723 172.54721851 -100.59115169 154.77492332 -84.66015625 138.8984375 C-83.64121261 137.88355292 -82.62223996 136.86869748 -81.60323906 135.85387039 C-76.8225408 131.0921173 -72.04418566 126.3280284 -67.26875216 121.56099546 C-61.78892514 116.09097461 -56.30087529 110.62937686 -50.80586535 105.17460954 C-46.53099936 100.92996589 -42.26389804 96.67761501 -38.00246328 92.4194876 C-35.46820002 89.8875627 -32.93081144 87.35897984 -30.38508797 84.83857346 C-27.5502999 82.03154066 -24.73177633 79.20869259 -21.91430664 76.38427734 C-21.08151688 75.56429764 -20.24872711 74.74431793 -19.39070129 73.89949036 C-12.57022097 67.01766646 -7.42016695 60.19746256 -6.75 50.3125 C-6.93514439 41.98100238 -9.96586313 35.94165382 -15.703125 29.953125 C-21.58674066 24.95005046 -28.4741069 23.14768806 -36.13040447 23.58917904 C-46.0270283 25.00601304 -52.82829889 32.09874332 -59.57104492 38.89233398 Z "
                        fill="#ffb5a0" transform="translate(318,149)" />
            </svg>
 '''),
                                ),
                                title: Text("NotesMaster"),
                                description: Text("View on Github (Archived)"),
                                trailing: Icon(Symbols.open_in_new),
                                onTap: () {
                                  openLink(
                                      "https://github.com/PranshulGG/NotesMaster");
                                })
                          ]),
                          Padding(
                            padding: EdgeInsets.only(
                              top: 16,
                              bottom:
                                  MediaQuery.of(context).padding.bottom + 10,
                              left: 16,
                              right: 16,
                            ),
                            child: SizedBox(
                              width: double.infinity,
                              child: FilledButton(
                                onPressed: () {
                                  Navigator.of(context).pop();
                                },
                                child: Text("close".tr()),
                              ),
                            ),
                          ),
                        ],
                      );
                    });
                  },
                );
              },
            )
          ],
        ),
      ),
      bottomNavigationBar: ClipRRect(
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(28),
          topRight: Radius.circular(28),
        ),
        child: BottomAppBar(
            elevation: 0,
            height: 180,
            padding: EdgeInsets.only(top: 10),
            color: Theme.of(context).colorScheme.surfaceContainer,
            child: ListView(physics: NeverScrollableScrollPhysics(), children: [
              ListTile(
                leading: CircleAvatar(radius: 23, child: Icon(Symbols.license)),
                title: Text("third_party_licenses".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      fullscreenDialog: true,
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return LicensePage(
                          applicationName: 'WeatherMaster',
                          applicationVersion: 'v2.6.4 (F)',
                          applicationIcon: Container(
                            clipBehavior: Clip.hardEdge,
                            margin: const EdgeInsets.only(bottom: 16, top: 16),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(50),
                            ),
                            child: Image.asset(
                              'assets/weather-icons/new_icon.png',
                              width: 60,
                              height: 60,
                            ),
                          ),
                        );
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
              ListTile(
                leading: CircleAvatar(
                  radius: 23,
                  child: Icon(Symbols.mail),
                ),
                title: Text("terms_&_conditions".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      opaque: true,
                      fullscreenDialog: true,
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return TermsPage();
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
              ListTile(
                leading: CircleAvatar(
                  radius: 23,
                  child: Icon(Symbols.code),
                ),
                title: Text("privacy_policy".tr()),
                onTap: () {
                  Navigator.of(context).push(
                    PageRouteBuilder(
                      opaque: true,
                      fullscreenDialog: true,
                      reverseTransitionDuration: Duration(milliseconds: 200),
                      pageBuilder: (context, animation, secondaryAnimation) {
                        return PolicyPage();
                      },
                      transitionsBuilder:
                          (context, animation, secondaryAnimation, child) {
                        return FadeTransition(
                          opacity: animation,
                          child: child,
                        );
                      },
                    ),
                  );
                },
              ),
            ])),
      ),
    );
  }
}

class TermsPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final markdownData = '''

These Terms & Conditions apply to the **WeatherMaster** app (the "Application") for mobile devices.  
This app was created by **Pranshul** as an open-source, hobby project. By using the Application, you agree to the following:

## Use of the Application
- The Application is provided **as-is**, free of charge, and without any guarantees of reliability, availability, or accuracy.  
- You may use, modify, and distribute the Application in accordance with its open-source license.  
- You may **not** misrepresent the origin of the Application or use its name/trademarks without permission.

## Data & Privacy
- The Application does **not collect, store, or share** any personal information.  
- The only permission requested is **location access**, which is optional and used solely within the Application to provide weather information. This data never leaves your device.  
- For more details, please see the Privacy Policy.

## Liability
- The Service Provider (Pranshul) is **not liable** for any direct or indirect damages, losses, or issues that may arise from using the Application.  
- This includes (but is not limited to) inaccurate weather data, device issues, or mobile data charges.  
- You are responsible for ensuring your device is compatible and has sufficient internet and battery to use the Application.

## Updates & Availability
- The Application may be updated from time to time.  
- There is no guarantee that the Application will always remain available, functional, or supported on all operating system versions.  
- The Service Provider may discontinue the Application at any time without prior notice.

## Changes to These Terms
These Terms & Conditions may be updated in the future. Updates will be posted in the project repository or within the Application. Continued use of the Application means you accept any revised terms.

## Contact
If you have any questions about these Terms & Conditions, please contact:  
📧 **pranshul.devmain@gmail.com**



''';

    return Scaffold(
      appBar: AppBar(
        title: Text('terms_&_conditions'.tr()),
        titleSpacing: 0,
        scrolledUnderElevation: 0,
      ),
      body: Markdown(
        data: markdownData,
        padding: EdgeInsets.only(
            top: 16,
            bottom: MediaQuery.of(context).padding.bottom + 10,
            left: 16,
            right: 16),
        onTapLink: (text, href, title) async {
          openLink(href.toString());
        },
      ),
    );
  }
}

class PolicyPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final markdownData = '''

WeatherMaster is an open-source application.  

We respect your privacy. This application:  

- Does **not collect, store, or share** any personal information.  
- Does **not track** your IP address, usage data, or any identifiers.  
- Does **not send data** to us or to third parties.  

## Location Permission  
- The app may request access to your device’s **location** in order to show you local weather information.  
- Granting location permission is **optional**.  
- Your location data is used **only within the app** to provide weather information and is **never collected, stored, or shared**.  

## Children  
This application does not knowingly collect any personal information from anyone, including children under 13.  

## Changes  
If this Privacy Policy changes, we will update it here. Continued use of the app after changes means you accept the revised policy.  

## Contact   
If you have any questions about privacy while using WeatherMaster, please contact us at:  
📧 **pranshul.devmain@gmail.com**  
''';

    return Scaffold(
      appBar: AppBar(
        title: Text('privacy_policy'.tr()),
        titleSpacing: 0,
        scrolledUnderElevation: 0,
      ),
      body: Markdown(
        data: markdownData,
        padding: EdgeInsets.only(
            top: 16,
            bottom: MediaQuery.of(context).padding.bottom + 10,
            left: 16,
            right: 16),
        onTapLink: (text, href, title) async {
          openLink(href.toString());
        },
      ),
    );
  }
}

class CheckUpdateButton extends StatefulWidget {
  @override
  _CheckUpdateButtonState createState() => _CheckUpdateButtonState();
}

class _CheckUpdateButtonState extends State<CheckUpdateButton> {
  final String currentVersion = 'v2.6.4';
  final String githubRepo = 'PranshulGG/WeatherMaster';
  bool isChecking = false;

  Future<void> checkForUpdates() async {
    setState(() {
      isChecking = true;
    });

    final String releasesUrl =
        'https://api.github.com/repos/$githubRepo/releases';

    try {
      final response = await http.get(Uri.parse(releasesUrl));
      if (response.statusCode != 200) {
        throw Exception('Failed to fetch releases');
      }

      final List<dynamic> releases = jsonDecode(response.body);
      final latestStable = releases.firstWhere(
        (release) => release['prerelease'] == false,
        orElse: () => null,
      );

      await Future.delayed(Duration(seconds: 2));

      if (latestStable != null && latestStable['tag_name'] != currentVersion) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('new_version_available!'.tr()),
            behavior: SnackBarBehavior.floating,
          ),
        );

        await Future.delayed(Duration(seconds: 1));

        final url = 'https://github.com/$githubRepo/releases';
        openLink(url);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text('you_are_using_the_latest_version!'.tr()),
              behavior: SnackBarBehavior.floating),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
            content: Text('error_checking_for_updates'.tr()),
            behavior: SnackBarBehavior.floating),
      );
      print('Error: $e');
    }

    setState(() {
      isChecking = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return FilledButton.icon(
      onPressed: isChecking ? null : checkForUpdates,
      icon: Icon(
        Symbols.refresh,
        weight: 700,
      ),
      label:
          Text(currentVersion, style: TextStyle(fontWeight: FontWeight.w700)),
      style: ButtonStyle(
        padding: WidgetStateProperty.all(
          EdgeInsets.symmetric(horizontal: 12, vertical: 5),
        ),
        shape: WidgetStateProperty.all(
          RoundedRectangleBorder(
            borderRadius: BorderRadius.only(
                topLeft: Radius.circular(50),
                topRight: Radius.circular(3),
                bottomLeft: Radius.circular(50),
                bottomRight: Radius.circular(3)),
          ),
        ),
        minimumSize: WidgetStateProperty.all(Size(0, 30)),
        iconAlignment: IconAlignment.end,
        tapTargetSize: MaterialTapTargetSize.shrinkWrap,
      ),
    );
  }
}

class ChangelogService {
  final String githubRepo = "PranshulGG/WeatherMaster";
  final Box _box = Hive.box('changelogs');

  Future<List<Map<String, dynamic>>> getChangelogs() async {
    final now = DateTime.now();
    final lastFetch = _box.get('lastFetch') as DateTime?;

    if (lastFetch != null &&
        now.difference(lastFetch) < const Duration(hours: 48)) {
      final cachedData = (_box.get('data', defaultValue: []) as List)
          .map((e) => Map<String, dynamic>.from(e as Map))
          .toList();

      await Future.delayed(const Duration(seconds: 2));
      return cachedData;
    }

    final url = Uri.parse("https://api.github.com/repos/$githubRepo/releases");
    final response = await http.get(url);

    if (response.statusCode != 200) {
      throw Exception("Failed to fetch releases");
    }

    final releases = jsonDecode(response.body) as List<dynamic>;

    final parsed = releases
        .where((r) => r['prerelease'] == false)
        .take(10)
        .map((r) => {
              "tag": r["tag_name"],
              "name": r["name"] ?? "",
              "body": r["body"] ?? "",
              "published_at": r["published_at"],
            })
        .toList();

    await _box.put('data', parsed);
    await _box.put('lastFetch', now);

    return parsed;
  }
}

class ChangelogSheet extends StatefulWidget {
  final ScrollController scrollController;
  const ChangelogSheet({super.key, required this.scrollController});

  @override
  State<ChangelogSheet> createState() => _ChangelogSheetState();
}

class _ChangelogSheetState extends State<ChangelogSheet> {
  final service = ChangelogService();
  late Future<List<Map<String, dynamic>>> _future;

  @override
  void initState() {
    super.initState();
    _future = service.getChangelogs();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _future,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return Center(
              child: ExpressiveLoadingIndicator(
            activeSize: 38,
            color: Theme.of(context).colorScheme.primary,
          ));
        }

        if (snapshot.hasError) {
          return Center(child: Text("Error loading changelog"));
        }

        final releases = snapshot.data as List<Map<String, dynamic>>;
        if (releases.isEmpty) {
          return const Center(child: Text("No changelogs available"));
        }

        final buffer = StringBuffer("");
        for (final release in releases) {
          buffer.writeln("# ${release['tag']}");
          buffer.writeln(release['body']);
          buffer.writeln("\n---\n");
        }

        final colorTheme = Theme.of(context).colorScheme;

        return ListView.builder(
          controller: widget.scrollController,
          itemCount: releases.length,
          itemBuilder: (context, index) {
            final release = releases[index];
            return Container(
              margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              padding: EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: colorTheme.surfaceContainerLowest,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(height: 6),
                  Row(spacing: 2.7, children: [
                    Container(
                      padding: const EdgeInsets.only(
                          left: 10, right: 10, bottom: 3, top: 3),
                      decoration: BoxDecoration(
                          color: colorTheme.primaryContainer,
                          borderRadius: BorderRadius.circular(50)),
                      child: Text(
                        release['tag'],
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color:
                              Theme.of(context).colorScheme.onPrimaryContainer,
                        ),
                      ),
                    ),
                    if (index == 0)
                      Container(
                        padding: const EdgeInsets.only(
                            left: 10, right: 10, bottom: 3, top: 3),
                        decoration: BoxDecoration(
                            color: colorTheme.tertiary,
                            borderRadius: BorderRadius.circular(8)),
                        child: Text(
                          "Latest",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Theme.of(context).colorScheme.onTertiary,
                          ),
                        ),
                      ),
                  ]),
                  SizedBox(height: 12),
                  MarkdownBody(
                    data: release['body'],
                    styleSheet: MarkdownStyleSheet.fromTheme(Theme.of(context))
                        .copyWith(
                      blockquote: TextStyle(
                        color: colorTheme.onSurfaceVariant,
                        fontStyle: FontStyle.italic,
                      ),
                      blockquoteDecoration: BoxDecoration(
                        color: Colors.transparent,
                        border: Border(
                            left: BorderSide(
                                color: colorTheme.outline, width: 4)),
                      ),
                    ),
                    onTapLink: (text, href, title) {
                      if (href != null) openLink(href);
                    },
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }
}

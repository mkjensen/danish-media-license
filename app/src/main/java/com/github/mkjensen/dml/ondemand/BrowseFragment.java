/*
 * Copyright 2016 Martin Kamp Jensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.dml.ondemand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;

import com.github.mkjensen.dml.R;

/**
 * Browse screen for on-demand videos.
 */
public class BrowseFragment extends BrowseSupportFragment {

  private static final String TAG = "BrowseFragment";

  private ArrayObjectAdapter rowsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    setTitle(getString(R.string.app_name));
    createRowsAdapter();
    addTestData();
    setupListeners();
  }

  private void createRowsAdapter() {
    rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
    setAdapter(rowsAdapter);
  }

  private void setupListeners() {
    setOnItemViewClickedListener(new OnItemViewClickedListener() {

      @Override
      public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                RowPresenter.ViewHolder rowViewHolder, Row row) {
        Video video = (Video) item;
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.VIDEO, video);
        startActivity(intent);
      }
    });
  }

  private void addTestData() {
    HeaderItem headerItem = new HeaderItem("Most viewed");
    VideoPresenter videoPresenter = new VideoPresenter();
    ArrayObjectAdapter videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .slug("bedrag-9-10")
        .title("Bedrag 9:10")
        .description("Alf begraver Mia og tynges af skyld, men Mads får ham ud af"
            + " selvmedlidenheden, da han gør en vild opdagelse. Energreen har ikke bare store"
            + " huller i regnskaberne - deres pengekasse er helt tom. Det kan de udnytte."
            + " Claudia er nået frem til samme konklusion om Energreen, men Sander insisterer på"
            + " at fortsætte det farlige spil - og sætter hende til at stikke en kæp i hjulet på"
            + " Bagmandspolitiets planer. Sanders håndlanger opdager, hvor Nicky og Bimse har deres"
            + " mange penge fra og insisterer på at få dem tilbage. Og Svenskeren er ikke en mand,"
            + " man siger nej til.\nSerien er uegnet for mindre børn.\nMedvirkende: Thomas Bo"
            + " Larsen, Natalie Madueño, Esben Smed, Nikolaj Lie Kaas m.fl.\nManuskript: Jeppe"
            + " Gjervig Gram, Anders August og Jannik Tai Mosholt\nInstruktion:"
            + " Søren Kragh-Jacobsen\nSe også: \nWeb: www.dr.dk/bedrag\nInstagram:"
            + " BEDRAGDR\nFacebook: https://www.facebook.com/bedragDR\nSendes med synstolkning"
            + " på DR1Syn")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56c330b2a11f9f0fc418f087")
        .videoUrl("http://drod10r-vh.akamaihd.net/i/all/clear/streaming/c3/56c6b5776187a4125474a8c3/"
            + "Bedrag--9-10-_e59f6c7223e14d3ab9af94ebc59bf006_,1125,562,2327,.mp4.csmil/master.m3u8"
            + "?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk/muTest/api/1.2/"
            + "subtitles/playlist/urn:dr:mu:manifest:56c6b5776187a4125474a8c3"
            + "?segmentsizeinms=60000")
        .build());
    videoAdapter.add(new Video.Builder()
        .slug("x-factor-2016-02-26")
        .title("X Factor")
        .description("Konkurrencen er i top, når X Factor i aften blænder op for årets andet"
            + " liveshow. Otte deltagere er tilbage, men kun syv kan fortsætte til næste uge, så"
            + " der er lagt op til masser af spænding, når deltagerne skal kaste sig ud i sange"
            + " fra de nordiske lande. Dommerne har gjort, hvad de kan for at forberede dem, så nu"
            + " er det op til dem selv at synge sig videre. Hvem der fortjener en plads videre, og"
            + " hvem der skal i farezonen, er op til dig derhjemme. Se med i aften på DR1.")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56cc4c076187a40a6cb56de5")
        .videoUrl("http://drod07q-vh.akamaihd.net/i/all/clear/streaming/2f/56d0d0c76187a51404d4892f/"
            + "X-Factor_e272491c43b74f80a181306e35baaf13_,1125,562,2278,.mp4.csmil/master.m3u8"
            + "?cc1=name=Dansk~default=yes~forced=no~lang=da~uri=http://www.dr.dk/muTest/api/1.2/"
            + "subtitles/playlist/urn:dr:mu:manifest:56d0d0c76187a51404d4892f"
            + "?segmentsizeinms=60000")
        .build());
    rowsAdapter.add(new ListRow(headerItem, videoAdapter));
    headerItem = new HeaderItem("Spotlight");
    videoAdapter = new ArrayObjectAdapter(videoPresenter);
    videoAdapter.add(new Video.Builder()
        .slug("x-y-z-1-2-3")
        .title("Apple master playlist")
        .description("HLS test")
        .imageUrl("https://i.ytimg.com/vi/8wlkr08hRPg/maxresdefault.jpg")
        .videoUrl("https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/"
            + "bipbop_4x3_variant.m3u8")
        .build());
    videoAdapter.add(new Video.Builder()
        .slug("dokumania-naturens-uorden")
        .title("Dokumania: Naturens uorden")
        .description("Dansk dokumentar fra 2015 af filminstruktøren Christian Sønderby"
            + " Jepsen\nDokumentaren skildrer spastikeren Jacob Nossells selvironiske kamp for at"
            + " forstå og iscenesætte sit eget liv. Ifølge videnskaben burde spastikeren Jacob"
            + " allerede fra fødslen være 'fravalgt' af både forskere og forældre. Med et intakt"
            + " intellekt, fanget i et svækket kropshylster, er han selveste legemliggørelsen af"
            + " normalitetens dilemma: For handicappet til at være normal og for normal til at"
            + " acceptere sig selv som handicappet. I forsøget på at forstå sig selv og sin egen"
            + " situation opsøger han førende danske videnskabsfolk og stiller dem spørgsmålet:"
            + " \"Er jeg unormal?\".")
        .imageUrl("http://www.dr.dk/muTest/api/1.2/bar/56b874266187a4086441a491")
        .videoUrl("http://drod03m-vh.akamaihd.net/i/dk/clear/streaming/53/56b592836187a409c0fc0b53/"
            + "Dokumania--Naturens-uorden_c3ff1414744c4456bec9568915b283fd_,1127,562,2276,.mp4"
            + ".csmil/master.m3u8")
        .build());
    rowsAdapter.add(new ListRow(headerItem, videoAdapter));
  }
}

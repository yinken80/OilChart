import { NgModule, ErrorHandler } from '@angular/core';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { Storage } from '@ionic/storage';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { OptionsMenuPage } from '../pages/options-menu/options-menu';
import { InformationPage } from '../pages/information/information';
import { AmChartsComponent } from "../components/amcharts.component";
import { OcAccordian } from "../components/OcAccordian/ocAccordian.component";
import { Utils } from "../providers/utils"; 
import { ChartService } from '../providers/chart.service';

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    OptionsMenuPage,
    InformationPage,
    AmChartsComponent,
    OcAccordian
  ],
  imports: [
    IonicModule.forRoot(MyApp)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage,
    OptionsMenuPage,
    InformationPage,
  ],
  providers: [{provide: ErrorHandler, useClass: IonicErrorHandler}, Utils, ChartService, Storage]
})
export class AppModule {}

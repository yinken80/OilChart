import { NgModule, ErrorHandler } from '@angular/core';
import { IonicApp, IonicModule, IonicErrorHandler } from 'ionic-angular';
import { MyApp } from './app.component';
import { HomePage } from '../pages/home/home';
import { AmChartsComponent } from "../components/amcharts.component";
import { OcAccordian } from "../components/OcAccordian/ocAccordian.component";
import { Utils } from "../providers/utils"; 
import { ChartService } from '../providers/chart.service';

@NgModule({
  declarations: [
    MyApp,
    HomePage,
    AmChartsComponent,
    OcAccordian
  ],
  imports: [
    IonicModule.forRoot(MyApp)
  ],
  bootstrap: [IonicApp],
  entryComponents: [
    MyApp,
    HomePage
  ],
  providers: [{provide: ErrorHandler, useClass: IonicErrorHandler}, Utils, ChartService]
})
export class AppModule {}

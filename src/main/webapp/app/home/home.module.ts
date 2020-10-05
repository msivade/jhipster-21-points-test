import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TwentyOnePointsSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { NvD3Module } from 'ng2-nvd3';
import 'd3';
import 'nvd3';

@NgModule({
  imports: [TwentyOnePointsSharedModule, NvD3Module, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
})
export class TwentyOnePointsHomeModule {}

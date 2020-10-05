import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

import { JhiEventManager } from 'ng-jhipster';
import { PointsService } from 'app/entities/points/points.service';
import { PreferencesService } from 'app/entities/preferences/preferences.service';
import { Preferences } from 'app/shared/model/preferences.model';
import { BloodPressureService } from 'app/entities/blood-pressure/blood-pressure.service';
import { D3ChartConfig, D3ChartService } from 'app/home/D3ChartService';

@Component({
  selector: 'jhi-home, ngbd-progressbar-showvalue',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  authSubscription?: Subscription;
  eventSubscriber?: Subscription;
  principal: any;
  pointsThisWeek: any = {};
  pointsPercentage?: number;
  preferences?: Preferences;
  bpReadings: any;
  bpOptions?: D3ChartConfig;
  bpData?: (
    | {
        color: string;
        values: any[];
        key: string;
      }
    | {
        color: string;
        values: any[];
        key: string;
      }
  )[];

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    private pointsService: PointsService,
    private preferencesService: PreferencesService,
    private bloodPressureService: BloodPressureService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      this.getUserData();
    });
    this.eventSubscriber = this.eventManager.subscribe('pointsListModification', () => this.getUserData());
    this.eventSubscriber = this.eventManager.subscribe('bloodPressureListModification', () => this.getUserData());
    this.eventSubscriber = this.eventManager.subscribe('weightListModification', () => this.getUserData());
    this.eventSubscriber = this.eventManager.subscribe('preferencesListModification', () => this.getUserData());
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  getUserData(): void {
    if (this.account) {
      // Get preferences
      this.preferencesService.user().subscribe((preferences: any) => (this.preferences = preferences.body));
      // Get points for the current week
      this.pointsService.thisWeek().subscribe((points: any) => {
        points = points.body;
        this.pointsThisWeek = points;
        this.pointsPercentage = (points.points / 21) * 100;

        // calculate success, warning, or danger
        if (this.preferences && this.preferences.weeklyGoal) {
          if (points.points >= this.preferences.weeklyGoal) {
            this.pointsThisWeek.progress = 'success';
          } else if (points.points < 10) {
            this.pointsThisWeek.progress = 'danger';
          } else if (points.points > 10 && points.p < this.preferences.weeklyGoal) {
            this.pointsThisWeek.progress = 'warning';
          }
        }
      });

      // Get blood pressure readings for the last 30 days
      // Get blood pressure readings for the last 30 days
      this.bloodPressureService.last30Days().subscribe((bpReadings: any) => {
        bpReadings = bpReadings.body;
        this.bpReadings = bpReadings;
        this.bpOptions = { ...D3ChartService.getChartConfig() };
        if (bpReadings.readings.length) {
          if (this.bpOptions.title) {
            this.bpOptions.title.text = bpReadings.period;
          }
          const systolics: any[] = [];
          const diastolics: any[] = [];
          const upperValues: number[] = [];
          const lowerValues: number[] = [];
          bpReadings.readings.forEach((item: any) => {
            systolics.push({
              x: new Date(item.timestamp),
              y: item.systolic,
            });
            diastolics.push({
              x: new Date(item.timestamp),
              y: item.diastolic,
            });
            upperValues.push(item.systolic);
            lowerValues.push(item.diastolic);
          });
          this.bpData = [
            {
              values: systolics,
              key: 'Systolic',
              color: '#673ab7',
            },
            {
              values: diastolics,
              key: 'Diastolic',
              color: '#03a9f4',
            },
          ];
          // set y scale to be 10 more than max and min
          if (this.bpOptions.chart) {
            this.bpOptions.chart.yAxis.axisLabel = 'Blood Pressure';
            this.bpOptions.chart.yDomain = [Math.min.apply(null, lowerValues) - 10, Math.max.apply(null, upperValues) + 10];
          }
        } else {
          this.bpReadings.readings = [];
        }
      });
    }
  }
}

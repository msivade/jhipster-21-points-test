import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

import { JhiEventManager } from 'ng-jhipster';
import { PointsService } from 'app/entities/points/points.service';
import { PreferencesService } from 'app/entities/preferences/preferences.service';
import { Preferences } from 'app/shared/model/preferences.model';

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

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    private pointsService: PointsService,
    private preferencesService: PreferencesService
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
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

import { JhiEventManager } from 'ng-jhipster';
import { PointsService } from 'app/entities/points/points.service';

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

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    private pointsService: PointsService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
      this.account = account;
      this.getUserData();
    });
    this.eventSubscriber = this.eventManager.subscribe('pointsListModification', () => this.getUserData());
    this.eventSubscriber = this.eventManager.subscribe('bloodPressureListModification', () => this.getUserData());
    this.eventSubscriber = this.eventManager.subscribe('weightListModification', () => this.getUserData());
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
    // Get points for the current week
    this.pointsService.thisWeek().subscribe((points: any) => {
      points = points.body;
      this.pointsThisWeek = points;
      this.pointsPercentage = (points.points / 21) * 100;
    });
  }
}

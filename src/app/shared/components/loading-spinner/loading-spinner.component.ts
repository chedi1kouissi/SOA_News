import { Component } from '@angular/core';

@Component({
    selector: 'app-loading-spinner',
    standalone: true,
    template: `
    <div class="spinner-container">
      <div class="spinner"></div>
    </div>
  `,
    styles: [`
    .spinner-container {
      display: flex;
      justify-content: center;
      padding: 40px;
    }
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #e2e8f0;
      border-top-color: #667eea;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class LoadingSpinnerComponent { }

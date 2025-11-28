import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';


@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {
    registerForm: FormGroup;
    error: string = '';
    isLoading: boolean = false;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {
        this.registerForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', [Validators.required, Validators.minLength(4)]]
        });
    }

    onSubmit() {
        if (this.registerForm.valid) {
            this.isLoading = true;
            this.error = '';

            this.authService.register(this.registerForm.value).subscribe({
                next: () => {
                    alert('Registration successful! Please login.');
                    this.router.navigate(['/login']);
                },
                error: (err) => {
                    this.error = err.error?.error || 'Registration failed. Please try again.';
                    this.isLoading = false;
                }
            });
        }
    }
}

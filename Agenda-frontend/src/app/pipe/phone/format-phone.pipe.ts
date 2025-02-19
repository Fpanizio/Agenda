import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatPhone',
  standalone: false,
})
export class FormatPhonePipe implements PipeTransform {
  transform(phone: string): string {
    if (!phone) return '';

    const cleaned = phone.replace(/\D/g, '');

    if (cleaned.length === 11) {
      return `(${cleaned.substring(0, 2)}) ${cleaned.substring(
        2,
        3
      )} ${cleaned.substring(3, 7)}-${cleaned.substring(7)}`;
    }

    return phone;
  }
}

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatDate',
  standalone: false,
})
export class FormatDatePipe implements PipeTransform {
  transform(date: string): string {
    if (!date) return '';

    const [year, month, day] = date.split('-');

    if (year && month && day) {
      return `${day}/${month}/${year}`;
    }

    return date;
  }
}

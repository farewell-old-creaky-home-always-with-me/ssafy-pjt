<script setup>
defineOptions({ inheritAttrs: false })
defineProps({
  variant: { type: String, default: 'primary' },
  size: { type: String, default: 'default' },
  full: { type: Boolean, default: false },
})
</script>

<template>
  <button
    v-bind="$attrs"
    :class="[
      'inline-flex items-center justify-center gap-2 font-semibold cursor-pointer transition-[background,box-shadow] duration-150 focus-visible:outline-2 focus-visible:outline-blue focus-visible:outline-offset-2',
      size === 'sm'
        ? 'px-4 py-2 text-[0.8125rem] rounded-[0.625rem]'
        : 'px-6 py-3 text-[0.9375rem] rounded-xl',
      full ? 'w-full' : '',
      variant === 'primary' && [
        'bg-blue text-white shadow-[0_2px_8px_rgba(45,156,219,0.3)]',
        'hover:bg-blue-hover hover:shadow-[0_4px_12px_rgba(45,156,219,0.4)]',
        'active:bg-blue-active active:shadow-[0_1px_4px_rgba(45,156,219,0.2)]',
        'disabled:bg-blue/70 disabled:shadow-none disabled:cursor-not-allowed',
      ],
      variant === 'navy' && [
        'bg-navy text-white',
        'hover:bg-[#15325A] active:bg-[#0F2645]',
        'disabled:opacity-50 disabled:cursor-not-allowed',
      ],
      variant === 'danger' && [
        'bg-red text-white',
        'hover:bg-[#D94A4A]',
        'disabled:bg-red/70 disabled:cursor-not-allowed',
      ],
      variant === 'ghost' && [
        'bg-bg-page text-[#555] border border-[#e5e7eb]',
        'hover:bg-[#e5e7eb] hover:text-[#333]',
      ],
      variant === 'outline-danger' && [
        'bg-white text-red border border-red/30',
        'hover:bg-red/5 hover:border-red',
      ],
    ]"
  >
    <slot />
  </button>
</template>

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["../resources/templates/**/*.{html,js}",
        "../resources/templates/**/**/*.{html,js}",
        "./node_modules/flowbite/**/*.js"],
    theme: {
        extend: {
            minWidth: {
                '1/4': '25%',
                '1/2': '50%',
                '3/4': '75%',
            },
            colors: {
                primary: '#3490dc',
            }
        }
    },
    plugins: [
        require('flowbite/plugin')
    ]
}
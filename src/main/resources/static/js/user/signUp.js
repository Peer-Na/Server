function signUp() {
    const id = Math.random() * 10000;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const name = document.getElementById('name').value;

    axios.post('/api/users', {
        id: id,
        email: email,
        password: password,
        name: name
    }).then(function (response) {
        console.log(response);
        alert('회원가입이 완료되었습니다.');
        window.location.href = '/login';
    }).catch(function (error) {
        console.log(error);
        alert('회원가입에 실패하였습니다.');
    });
}
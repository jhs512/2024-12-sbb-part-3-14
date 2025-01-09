    const page_elements = document.getElementsByClassName("page-link");
    Array.from(page_elements).forEach(function(element) {
        element.addEventListener('click', function() {
            document.getElementById('page').value = this.dataset.page;
            document.getElementById('searchForm').submit();
        });
    });
    const comment_elements = document.getElementsByName("show_comment");
    Array.from(comment_elements).forEach((comment, index) => {
        comment.addEventListener('click', function() {
              const btn1 = document.getElementsByName('commit_comment'+index)[0];
              if(btn1.style.display != 'none') {
                btn1.style.display = 'none';
              }
              // btn` 보이기 (display: block)
              else {
                btn1.style.display = 'block';
              }
        });
    });